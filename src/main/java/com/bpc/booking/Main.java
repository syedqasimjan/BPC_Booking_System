package com.bpc.booking;

import com.bpc.booking.model.*;
import com.bpc.booking.service.*;
import com.bpc.booking.util.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        // Initialize the clinic and load data
        Clinic clinic = new Clinic();
        List<Physiotherapist> physios = DataLoader.loadPhysiotherapists();
        DataLoader.loadTreatments(physios);
        List<Patient> patients = DataLoader.loadPatients();
        clinic.setPhysiotherapists(physios);
        clinic.setPatients(patients);

        // Print loaded patients to debug
        System.out.println("Loaded patients:");
        for (Patient p : patients) {
            System.out.println(" - " + p.getId() + ": " + p.getName());
        }

        // Load existing appointments
        clinic.loadAppointments(physios, patients);

        // Load the availability timetable from timetable.txt
        System.out.println("\nLoading availability timetable from timetable.txt...");
        DataLoader.loadTimetable(clinic);

        // Add sample bookings only if they don't already exist
        System.out.println("\nAdding sample bookings...");
        try {
            // Check if appointments already exist
            boolean hasSampleAppointments = clinic.getAppointments().stream().anyMatch(a ->
                    a.getDateTime().equals("2025-04-01 10:00") && a.getPhysiotherapist().getId().equals("ID001") ||
                            a.getDateTime().equals("2025-04-01 09:00") && a.getPhysiotherapist().getId().equals("ID002") ||
                            a.getDateTime().equals("2025-04-01 13:00") && a.getPhysiotherapist().getId().equals("ID003"));

            if (!hasSampleAppointments) {
                // Book for Dr. Michael Harper
                Patient patient1 = patients.stream().filter(p -> p.getId().equals("ID101")).findFirst().orElseThrow();
                Physiotherapist physio1 = physios.stream().filter(p -> p.getId().equals("ID001")).findFirst().orElseThrow();
                Treatment treatment1 = physio1.getTreatments().get(0); // Deep Tissue Massage
                clinic.bookAppointment(patient1, physio1, treatment1, "2025-04-01 10:00");
                clinic.getAppointments().stream()
                        .filter(a -> a.getDateTime().equals("2025-04-01 10:00") && a.getPhysiotherapist().getId().equals("ID001"))
                        .findFirst()
                        .ifPresent(a -> a.changeStatus(Status.ATTENDED));

                // Book for Dr. Sarah Mitchell
                Patient patient2 = patients.stream().filter(p -> p.getId().equals("ID102")).findFirst().orElseThrow();
                Physiotherapist physio2 = physios.stream().filter(p -> p.getId().equals("ID002")).findFirst().orElseThrow();
                Treatment treatment2 = physio2.getTreatments().get(0); // Pool Therapy
                clinic.bookAppointment(patient2, physio2, treatment2, "2025-04-01 09:00");
                clinic.getAppointments().stream()
                        .filter(a -> a.getDateTime().equals("2025-04-01 09:00") && a.getPhysiotherapist().getId().equals("ID002"))
                        .findFirst()
                        .ifPresent(a -> a.changeStatus(Status.BOOKED));

                // Book for Dr. Andrew Kim
                Patient patient3 = patients.stream().filter(p -> p.getId().equals("ID103")).findFirst().orElseThrow();
                Physiotherapist physio3 = physios.stream().filter(p -> p.getId().equals("ID003")).findFirst().orElseThrow();
                Treatment treatment3 = physio3.getTreatments().get(0); // Pediatric Massage
                clinic.bookAppointment(patient3, physio3, treatment3, "2025-04-01 13:00");
                clinic.getAppointments().stream()
                        .filter(a -> a.getDateTime().equals("2025-04-01 13:00") && a.getPhysiotherapist().getId().equals("ID003"))
                        .findFirst()
                        .ifPresent(a -> a.changeStatus(Status.CANCELLED));

                // Save the appointments immediately after adding sample bookings
                clinic.saveAppointments();
                System.out.println("Sample bookings added successfully!");
            } else {
                System.out.println("Sample bookings already exist, skipping addition.");
            }
        } catch (Exception e) {
            System.out.println("Error adding sample bookings: " + e.getMessage());
        }

        // Create a copy of the appointments list to restore after displaying the timetable
        List<Appointment> originalAppointments = new ArrayList<>(clinic.getAppointments());

        // Display the timetable
        displayTimetable(clinic);

        // Restore the original appointments list to ensure temporary cancellations are not saved
        clinic.setAppointments(originalAppointments);

        // Save the appointments again to ensure only the original appointments are persisted
        clinic.saveAppointments();

        // Generate the end-of-term report
        ReportService reportService = new ReportService(clinic);
        reportService.generateReport();

    }

    private static void displayTimetable(Clinic clinic) {
        System.out.println("\n=== Availability Timetable (April 1 - April 28, 2025) ===");
        LocalDateTime startDate = LocalDateTime.of(2025, 4, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 4, 28, 23, 59);

        for (Physiotherapist physio : clinic.getPhysiotherapists()) {
            System.out.println("\nPhysiotherapist: " + physio.getName() + " (Phone: " + physio.getPhoneNumber() + ")");
            List<Availability> physioAvailabilities = clinic.getAvailabilities().stream()
                    .filter(a -> a.getPhysioId().equals(physio.getId()))
                    .filter(a -> !a.getDate().isBefore(startDate.toLocalDate()) && !a.getDate().isAfter(endDate.toLocalDate()))
                    .sorted(Comparator.comparing(Availability::getDate).thenComparing(Availability::getStartTime))
                    .collect(Collectors.toList());

            List<Appointment> physioAppointments = clinic.getAppointments().stream()
                    .filter(a -> a.getPhysiotherapist().equals(physio))
                    .filter(a -> {
                        LocalDateTime appointmentDate = LocalDateTime.parse(a.getDateTime(), formatter);
                        return !appointmentDate.isBefore(startDate) && !appointmentDate.isAfter(endDate);
                    })
                    .sorted(Comparator.comparing(a -> LocalDateTime.parse(a.getDateTime(), formatter)))
                    .collect(Collectors.toList());

            // Display availability and appointments by week
            for (int week = 1; week <= 4; week++) {
                LocalDateTime weekStart = startDate.plusDays((week - 1) * 7L);
                LocalDateTime weekEnd = weekStart.plusDays(6).withHour(23).withMinute(59);

                System.out.println("Week " + week + " (" + weekStart.format(formatter) + " to " + weekEnd.format(formatter) + "):");

                // Display availability
                System.out.println("Availability:");
                List<Availability> weekAvailabilities = physioAvailabilities.stream()
                        .filter(a -> !a.getDate().isBefore(weekStart.toLocalDate()) && !a.getDate().isAfter(weekEnd.toLocalDate()))
                        .collect(Collectors.toList());

                if (weekAvailabilities.isEmpty()) {
                    System.out.println(" - No availability.");
                } else {
                    for (Availability a : weekAvailabilities) {
                        System.out.println(" - " + a.getDate() + " from " + a.getStartTime() + " to " + a.getEndTime());
                    }
                }

                // Display scheduled appointments
                System.out.println("Scheduled Appointments:");
                List<Appointment> weekAppointments = physioAppointments.stream()
                        .filter(a -> {
                            LocalDateTime appointmentDate = LocalDateTime.parse(a.getDateTime(), formatter);
                            return !appointmentDate.isBefore(weekStart) && !appointmentDate.isAfter(weekEnd);
                        })
                        .collect(Collectors.toList());

                if (weekAppointments.isEmpty()) {
                    System.out.println(" - No appointments.");
                } else {
                    for (Appointment a : weekAppointments) {
                        System.out.println(" - " + a.getTreatment().getName() + " with " + a.getPatient().getName() +
                                " at " + a.getDateTime() + " (" + a.getStatus() + ")");
                    }
                }
            }

            // Display available time slots (sample within the term: April 1 - April 4, 2025)
            System.out.println("\nAvailable Time Slots (Sample for April 1 - April 4, 2025):");
            LocalDateTime availStart = LocalDateTime.of(2025, 4, 1, 9, 0);
            LocalDateTime availEnd = LocalDateTime.of(2025, 4, 4, 17, 0);
            List<String> availableSlots = new ArrayList<>();
            Patient dummyPatient = clinic.getPatients().get(0);
            Treatment dummyTreatment = physio.getTreatments().get(0);

            // Create a temporary copy of the appointments list to avoid modifying the actual list
            List<Appointment> tempAppointments = new ArrayList<>(clinic.getAppointments());

            LocalDateTime current = availStart;
            while (current.isBefore(availEnd)) {
                if (current.getDayOfWeek().getValue() <= 5) { // Monday-Friday
                    if (current.getHour() >= 9 && current.getHour() < 17) { // 9 AM-5 PM
                        String dateTime = current.format(formatter);
                        try {
                            // Temporarily set the appointments list to the copy
                            clinic.setAppointments(tempAppointments);
                            clinic.bookAppointment(dummyPatient, physio, dummyTreatment, dateTime);
                            clinic.cancelAppointment(dateTime, physio);
                            availableSlots.add(dateTime);
                        } catch (Exception e) {
                            // Slot is not available
                        } finally {
                            // Restore the temporary appointments list for the next iteration
                            clinic.setAppointments(tempAppointments);
                        }
                    }
                    current = current.plusHours(1);
                } else {
                    current = current.plusDays(1).withHour(9).withMinute(0);
                }
            }

            if (availableSlots.isEmpty()) {
                System.out.println(" - No available slots.");
            } else {
                for (String slot : availableSlots) {
                    System.out.println(" - " + slot);
                }
            }
        }
    }
}