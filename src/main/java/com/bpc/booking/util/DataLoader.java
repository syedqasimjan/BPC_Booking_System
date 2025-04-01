package com.bpc.booking.util;

import com.bpc.booking.model.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {
    public static List<Physiotherapist> loadPhysiotherapists() {
        List<Physiotherapist> physios = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/data/physiotherapists.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    // The address field may contain commas, so join parts 2 to (length-3) for the address
                    StringBuilder addressBuilder = new StringBuilder();
                    for (int i = 2; i < parts.length - 2; i++) {
                        addressBuilder.append(parts[i]);
                        if (i < parts.length - 3) {
                            addressBuilder.append(",");
                        }
                    }
                    String address = addressBuilder.toString().trim();
                    // The second-to-last part is the phone number
                    String phoneNumber = parts[parts.length - 2].trim();
                    // The last part is the expertise list
                    String[] expertise = parts[parts.length - 1].trim().split(";");

                    Physiotherapist physio = new Physiotherapist(id, name, address, phoneNumber);
                    for (String exp : expertise) {
                        physio.addExpertise(exp.trim());
                    }
                    physios.add(physio);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return physios;
    }

    public static void loadTreatments(List<Physiotherapist> physios) {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/data/treatments.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String physioId = parts[0].trim();
                    String name = parts[1].trim();
                    String expertise = parts[2].trim();
                    int duration = Integer.parseInt(parts[3].trim());
                    double cost = Double.parseDouble(parts[4].trim());

                    Treatment treatment = new Treatment(name, expertise, duration, cost);
                    physios.stream()
                            .filter(p -> p.getId().equals(physioId))
                            .findFirst()
                            .ifPresent(p -> p.addTreatment(treatment));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Patient> loadPatients() {
        List<Patient> patients = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/data/patients.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    // The address field may contain commas, so join parts 2 to (length-2) for the address
                    StringBuilder addressBuilder = new StringBuilder();
                    for (int i = 2; i < parts.length - 1; i++) {
                        addressBuilder.append(parts[i]);
                        if (i < parts.length - 2) {
                            addressBuilder.append(",");
                        }
                    }
                    String address = addressBuilder.toString().trim();
                    String phoneNumber = parts[parts.length - 1].trim();

                    Patient patient = new Patient(id, name, address, phoneNumber);
                    patients.add(patient);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return patients;
    }

    public static void loadTimetable(Clinic clinic) {
        List<Availability> availabilities = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/data/timetable.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String physioId = parts[0].trim();
                    LocalDate date = LocalDate.parse(parts[1].trim());
                    LocalTime startTime = LocalTime.parse(parts[2].trim());
                    LocalTime endTime = LocalTime.parse(parts[3].trim());
                    availabilities.add(new Availability(physioId, date, startTime, endTime));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        clinic.setAvailabilities(availabilities);
    }
}