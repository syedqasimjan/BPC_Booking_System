package com.bpc.booking;

import com.bpc.booking.model.*;
import com.bpc.booking.service.*;
import com.bpc.booking.util.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MenuBasedMain {
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

        // Initialize services
        BookingService bookingService = new BookingService(clinic);
        ReportService reportService = new ReportService(clinic);

        // Menu loop
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Boost Physio Clinic Booking System ===");
            System.out.println("1. Book Appointment by Expertise");
            System.out.println("2. Book Appointment by Physiotherapist Name");
            System.out.println("3. Cancel Appointment");
            System.out.println("4. Mark Appointment as Attended");
            System.out.println("5. Generate Report");
            System.out.println("6. View Availability Timetable");
            System.out.println("7. Add Patient");
            System.out.println("8. Remove Patient");
            System.out.println("9. Change Appointment");
            System.out.println("10. Exit");
            System.out.print("Choose an option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    try {
                        System.out.print("Enter expertise (e.g., Physiotherapy, Rehabilitation): ");
                        String expertise = scanner.nextLine();
                        System.out.print("Enter patient ID (e.g., ID101 for Emily Carter): ");
                        String patientId = scanner.nextLine();
                        bookingService.bookByExpertise(expertise, patientId);
                        System.out.println("Appointment booked successfully!");
                        clinic.saveAppointments();
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 2:
                    try {
                        System.out.print("Enter physiotherapist name (e.g., Dr. Michael Harper): ");
                        String physioName = scanner.nextLine();
                        System.out.print("Enter patient ID (e.g., ID101 for Emily Carter): ");
                        String patientId = scanner.nextLine();
                        bookingService.bookByPhysioName(physioName, patientId);
                        System.out.println("Appointment booked successfully!");
                        clinic.saveAppointments();
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 3:
                    try {
                        System.out.print("Enter date and time of appointment to cancel (e.g., 2025-04-01 10:00): ");
                        String dateTime = scanner.nextLine();
                        System.out.print("Enter physiotherapist name (e.g., Dr. Michael Harper): ");
                        String physioName = scanner.nextLine();
                        Physiotherapist physio = clinic.searchByPhysioName(physioName);
                        if (physio != null) {
                            clinic.cancelAppointment(dateTime, physio);
                            System.out.println("Appointment canceled successfully!");
                            clinic.saveAppointments();
                        } else {
                            System.out.println("Physiotherapist not found!");
                        }
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 4:
                    try {
                        System.out.print("Enter date and time of appointment to mark as attended (e.g., 2025-04-01 10:00): ");
                        String dateTime = scanner.nextLine();
                        Appointment appointment = findAppointmentByDateTime(clinic, dateTime);
                        if (appointment != null) {
                            appointment.changeStatus(Status.ATTENDED);
                            System.out.println("Appointment marked as attended!");
                            clinic.saveAppointments();
                        } else {
                            System.out.println("Appointment not found!");
                        }
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 5:
                    reportService.generateReport();
                    break;

                case 6:
                    // Create a copy of the appointments list to restore after displaying the timetable
                    List<Appointment> originalAppointments = new ArrayList<>(clinic.getAppointments());
                    displayTimetable(clinic);
                    // Restore the original appointments list
                    clinic.setAppointments(originalAppointments);
                    break;

                case 7:
                    try {
                        System.out.print("Enter patient ID (e.g., ID113): ");
                        String id = scanner.nextLine();
                        System.out.print("Enter patient name (e.g., John Doe): ");
                        String name = scanner.nextLine();
                        System.out.print("Enter patient address (e.g., 123 Main St, Toronto, ON M1M 1M1): ");
                        String address = scanner.nextLine();
                        System.out.print("Enter patient phone number (e.g., 416-555-0113): ");
                        String phoneNumber = scanner.nextLine();
                        Patient patient = new Patient(id, name, address, phoneNumber);
                        clinic.addPatient(patient);
                        clinic.savePatients();
                        System.out.println("Patient added successfully!");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 8:
                    try {
                        System.out.print("Enter patient ID to remove (e.g., ID113): ");
                        String patientId = scanner.nextLine();
                        clinic.removePatient(patientId);
                        clinic.savePatients();
                        System.out.println("Patient removed successfully!");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 9:
                    try {
                        System.out.print("Enter date and time of appointment to change (e.g., 2025-04-01 10:00): ");
                        String oldDateTime = scanner.nextLine();
                        System.out.print("Enter physiotherapist name (e.g., Dr. Michael Harper): ");
                        String physioName = scanner.nextLine();
                        Physiotherapist physio = clinic.searchByPhysioName(physioName);
                        if (physio == null) {
                            System.out.println("Physiotherapist not found!");
                            break;
                        }
                        Appointment appointment = findAppointmentByDateTime(clinic, oldDateTime);
                        if (appointment == null || !appointment.getPhysiotherapist().equals(physio)) {
                            System.out.println("Appointment not found for this physiotherapist!");
                            break;
                        }
                        clinic.cancelAppointment(oldDateTime, physio);
                        System.out.print("Enter new date and time (e.g., 2025-04-29 10:00): ");
                        String newDateTime = scanner.nextLine();
                        clinic.bookAppointment(appointment.getPatient(), physio, appointment.getTreatment(), newDateTime);
                        System.out.println("Appointment changed successfully!");
                        clinic.saveAppointments();
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 10:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private static Appointment findAppointmentByDateTime(Clinic clinic, String dateTime) {
        for (Appointment a : clinic.getAppointments()) {
            if (a.getDateTime().equals(dateTime)) {
                return a;
            }
        }
        return null;
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