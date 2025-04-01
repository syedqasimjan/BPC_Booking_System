package com.bpc.booking.model;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Clinic {
    private List<Physiotherapist> physiotherapists;
    private List<Patient> patients;
    private List<Appointment> appointments;
    private List<Availability> availabilities;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Clinic() {
        this.physiotherapists = new ArrayList<>();
        this.patients = new ArrayList<>();
        this.appointments = new ArrayList<>();
        this.availabilities = new ArrayList<>();
    }

    public List<Physiotherapist> getPhysiotherapists() {
        return physiotherapists;
    }

    public void setPhysiotherapists(List<Physiotherapist> physiotherapists) {
        this.physiotherapists = physiotherapists;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public List<Availability> getAvailabilities() {
        return availabilities;
    }

    public void setAvailabilities(List<Availability> availabilities) {
        this.availabilities = availabilities;
    }

    public void addPatient(Patient patient) {
        patients.add(patient);
    }

    public void removePatient(String patientId) {
        patients.removeIf(p -> p.getId().equals(patientId));
    }

    public void bookAppointment(Patient patient, Physiotherapist physio, Treatment treatment, String dateTime) {
        LocalDateTime appointmentDateTime = LocalDateTime.parse(dateTime, formatter);
        LocalDateTime appointmentEnd = appointmentDateTime.plusMinutes(treatment.getDuration());

        // Check if the physiotherapist has the required expertise
        if (!physio.getExpertise().contains(treatment.getExpertise())) {
            throw new IllegalArgumentException("Physiotherapist does not have the required expertise: " + treatment.getExpertise());
        }

        // Check availability
        boolean isAvailable = false;
        for (Availability availability : availabilities) {
            if (availability.getPhysioId().equals(physio.getId()) &&
                    availability.getDate().equals(appointmentDateTime.toLocalDate())) {
                if (!appointmentDateTime.toLocalTime().isBefore(availability.getStartTime()) &&
                        !appointmentEnd.toLocalTime().isAfter(availability.getEndTime())) {
                    isAvailable = true;
                    break;
                }
            }
        }
        if (!isAvailable) {
            throw new IllegalArgumentException("Physiotherapist is not available at the requested time.");
        }

        // Check for overlapping appointments
        for (Appointment existing : appointments) {
            if (existing.getPhysiotherapist().equals(physio)) {
                LocalDateTime existingStart = LocalDateTime.parse(existing.getDateTime(), formatter);
                LocalDateTime existingEnd = existingStart.plusMinutes(existing.getTreatment().getDuration());
                if (!(appointmentEnd.isBefore(existingStart) || appointmentDateTime.isAfter(existingEnd))) {
                    throw new IllegalArgumentException("Physiotherapist already has an appointment at the requested time.");
                }
            }
        }

        Appointment appointment = new Appointment(patient, physio, treatment, dateTime);
        appointments.add(appointment);
    }

    public void cancelAppointment(String dateTime, Physiotherapist physio) {
        Appointment appointment = appointments.stream()
                .filter(a -> a.getDateTime().equals(dateTime) && a.getPhysiotherapist().equals(physio))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found."));
        appointment.changeStatus(Status.CANCELLED);
    }

    public Physiotherapist searchByPhysioName(String name) {
        return physiotherapists.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public List<Physiotherapist> searchByExpertise(String expertise) {
        return physiotherapists.stream()
                .filter(p -> p.getExpertise().contains(expertise))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public void saveAppointments() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/data/appointments.txt"))) {
            for (Appointment appointment : appointments) {
                writer.write(appointment.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAppointments(List<Physiotherapist> physios, List<Patient> patients) {
        appointments = new ArrayList<>();
        File file = new File("src/main/resources/data/appointments.txt");
        if (!file.exists()) {
            System.out.println("appointments.txt not found. Starting with an empty appointment list.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                // Minimum required fields: 12 (without physio phone number), 13 (with physio phone number)
                if (parts.length >= 12) {
                    try {
                        // Work backward from the end to handle commas in addresses
                        // Last 3 fields (or 2 if physioPhoneNumber is missing) are fixed: dateTime, status, (physioPhoneNumber)
                        int statusIndex = parts.length - 2; // Second-to-last field is status
                        int dateTimeIndex = parts.length - 3; // Third-to-last field is dateTime
                        int costIndex = parts.length - 4; // Fourth-to-last field is cost
                        int durationIndex = parts.length - 5; // Fifth-to-last field is duration
                        int treatmentExpertiseIndex = parts.length - 6; // Sixth-to-last field is treatmentExpertise
                        int treatmentNameIndex = parts.length - 7; // Seventh-to-last field is treatmentName

                        // Extract the fixed fields at the end
                        String dateTime = parts[dateTimeIndex].trim();
                        Status status = Status.valueOf(parts[statusIndex].trim());
                        String physioPhoneNumber = parts.length == 13 ? parts[parts.length - 1].trim() : "";
                        double cost = Double.parseDouble(parts[costIndex].trim());
                        int duration = Integer.parseInt(parts[durationIndex].trim());
                        String treatmentExpertise = parts[treatmentExpertiseIndex].trim();
                        String treatmentName = parts[treatmentNameIndex].trim();

                        // Physiotherapist fields (physioId and physioName are fixed, but physioAddress may contain commas)
                        int physioNameIndex = treatmentNameIndex - 1; // Right before treatmentName
                        int physioIdIndex = physioNameIndex - 1; // Right before physioName
                        String physioName = parts[physioNameIndex].trim();
                        String physioId = parts[physioIdIndex].trim();

                        // Patient fields (patientId and patientName are fixed, patientAddress may contain commas)
                        int patientPhoneNumberIndex = physioIdIndex - 1; // Right before physioId
                        String patientPhoneNumber = parts[patientPhoneNumberIndex].trim();
                        int patientNameIndex = 1; // Second field is patientName
                        String patientId = parts[0].trim();
                        String patientName = parts[patientNameIndex].trim();

                        // Reconstruct patientAddress (from after patientName to before patientPhoneNumber)
                        StringBuilder patientAddressBuilder = new StringBuilder();
                        for (int i = patientNameIndex + 1; i < patientPhoneNumberIndex; i++) {
                            patientAddressBuilder.append(parts[i]);
                            if (i < patientPhoneNumberIndex - 1) {
                                patientAddressBuilder.append(",");
                            }
                        }
                        String patientAddress = patientAddressBuilder.toString().trim();

                        // Find patient and physiotherapist
                        Patient patient = patients.stream()
                                .filter(p -> p.getId().equals(patientId))
                                .findFirst()
                                .orElse(null);
                        Physiotherapist physio = physios.stream()
                                .filter(p -> p.getId().equals(physioId))
                                .findFirst()
                                .orElse(null);

                        if (patient == null || physio == null) {
                            System.out.println("Failed to load appointment: " + line);
                            continue;
                        }

                        Treatment treatment = new Treatment(treatmentName, treatmentExpertise, duration, cost);
                        Appointment appointment = new Appointment(patient, physio, treatment, dateTime);
                        appointment.changeStatus(status);
                        appointments.add(appointment);
                    } catch (Exception e) {
                        System.out.println("Failed to load appointment: " + line);
                        e.printStackTrace(); // Print stack trace for debugging
                    }
                } else {
                    System.out.println("Failed to load appointment: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePatients() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/data/patients.txt"))) {
            for (Patient patient : patients) {
                writer.write(patient.getId() + "," + patient.getName() + "," + patient.getAddress() + "," + patient.getPhoneNumber());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}