package com.bpc.booking.service;

import com.bpc.booking.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class ReportService {
    private final Clinic clinic;

    public ReportService(Clinic clinic) {
        this.clinic = clinic;
    }

    public void generateReport() {
        System.out.println("\n=================================================================");
        System.out.println("          Boost Physio Clinic End of Term Report");
        System.out.println("          Period: April 1, 2025 - April 28, 2025");
        System.out.println("=================================================================\n");

        // Group appointments by physiotherapist
        Map<Physiotherapist, List<Appointment>> appointmentsByPhysio = clinic.getAppointments().stream()
                .collect(Collectors.groupingBy(Appointment::getPhysiotherapist));

        // Sort physiotherapists by number of attended appointments (descending), then by name (alphabetical)
        List<Map.Entry<Physiotherapist, List<Appointment>>> sortedPhysios = appointmentsByPhysio.entrySet().stream()
                .sorted((e1, e2) -> {
                    long attended1 = e1.getValue().stream().filter(a -> a.getStatus() == Status.ATTENDED).count();
                    long attended2 = e2.getValue().stream().filter(a -> a.getStatus() == Status.ATTENDED).count();
                    int compare = Long.compare(attended2, attended1); // Descending order
                    if (compare == 0) {
                        return e1.getKey().getName().compareTo(e2.getKey().getName()); // Alphabetical if tied
                    }
                    return compare;
                })
                .collect(Collectors.toList());

        // Overall statistics
        long totalAppointments = clinic.getAppointments().size();
        long totalAttended = clinic.getAppointments().stream().filter(a -> a.getStatus() == Status.ATTENDED).count();
        long totalBooked = clinic.getAppointments().stream().filter(a -> a.getStatus() == Status.BOOKED).count();
        long totalCancelled = clinic.getAppointments().stream().filter(a -> a.getStatus() == Status.CANCELLED).count();
        double totalRevenue = clinic.getAppointments().stream()
                .filter(a -> a.getStatus() == Status.ATTENDED)
                .mapToDouble(a -> a.getTreatment().getCost())
                .sum();

        // Report for each physiotherapist
        for (Map.Entry<Physiotherapist, List<Appointment>> entry : sortedPhysios) {
            Physiotherapist physio = entry.getKey();
            List<Appointment> physioAppointments = entry.getValue();

            System.out.println("-----------------------------------------------------------------");
            System.out.println("Physiotherapist Details:");
            System.out.println("-----------------------------------------------------------------");
            System.out.println("Name: " + physio.getName());
            System.out.println("Phone: " + physio.getPhoneNumber());
            System.out.println("Expertise: " + String.join(", ", physio.getExpertise()));
            System.out.println();

            // Group appointments by status
            Map<Status, List<Appointment>> appointmentsByStatus = physioAppointments.stream()
                    .collect(Collectors.groupingBy(Appointment::getStatus));

            // Display appointments by status
            System.out.println("Appointments:");
            System.out.println("-----------------------------------------------------------------");

            // Attended Appointments
            System.out.println("Attended Appointments:");
            List<Appointment> attendedAppointments = appointmentsByStatus.getOrDefault(Status.ATTENDED, new ArrayList<>());
            if (attendedAppointments.isEmpty()) {
                System.out.println(" - None");
            } else {
                attendedAppointments.sort(Comparator.comparing(Appointment::getDateTime));
                for (Appointment a : attendedAppointments) {
                    System.out.printf(" - %s with %s at %s (Cost: $%.2f)%n",
                            a.getTreatment().getName(),
                            a.getPatient().getName(),
                            a.getDateTime(),
                            a.getTreatment().getCost());
                }
            }

            // Booked Appointments
            System.out.println("\nBooked Appointments:");
            List<Appointment> bookedAppointments = appointmentsByStatus.getOrDefault(Status.BOOKED, new ArrayList<>());
            if (bookedAppointments.isEmpty()) {
                System.out.println(" - None");
            } else {
                bookedAppointments.sort(Comparator.comparing(Appointment::getDateTime));
                for (Appointment a : bookedAppointments) {
                    System.out.printf(" - %s with %s at %s (Cost: $%.2f)%n",
                            a.getTreatment().getName(),
                            a.getPatient().getName(),
                            a.getDateTime(),
                            a.getTreatment().getCost());
                }
            }

            // Cancelled Appointments
            System.out.println("\nCancelled Appointments:");
            List<Appointment> cancelledAppointments = appointmentsByStatus.getOrDefault(Status.CANCELLED, new ArrayList<>());
            if (cancelledAppointments.isEmpty()) {
                System.out.println(" - None");
            } else {
                cancelledAppointments.sort(Comparator.comparing(Appointment::getDateTime));
                for (Appointment a : cancelledAppointments) {
                    System.out.printf(" - %s with %s at %s (Cost: $%.2f)%n",
                            a.getTreatment().getName(),
                            a.getPatient().getName(),
                            a.getDateTime(),
                            a.getTreatment().getCost());
                }
            }

            // Summary statistics
            long attendedCount = attendedAppointments.size();
            long bookedCount = bookedAppointments.size();
            long cancelledCount = cancelledAppointments.size();
            long totalPhysioAppointments = physioAppointments.size();
            double physioRevenue = attendedAppointments.stream()
                    .mapToDouble(a -> a.getTreatment().getCost())
                    .sum();

            System.out.println("\n-----------------------------------------------------------------");
            System.out.println("Summary for " + physio.getName() + ":");
            System.out.println("-----------------------------------------------------------------");
            System.out.println("Total Appointments: " + totalPhysioAppointments);
            System.out.println(" - Attended: " + attendedCount);
            System.out.println(" - Booked: " + bookedCount);
            System.out.println(" - Cancelled: " + cancelledCount);
            System.out.printf("Total Revenue: $%.2f%n", physioRevenue);
            System.out.println();
        }

        // Overall clinic summary
        System.out.println("=================================================================");
        System.out.println("Overall Clinic Summary:");
        System.out.println("=================================================================");
        System.out.println("Total Physiotherapists: " + clinic.getPhysiotherapists().size());
        System.out.println("Total Patients: " + clinic.getPatients().size());
        System.out.println("Total Appointments: " + totalAppointments);
        System.out.println(" - Attended: " + totalAttended);
        System.out.println(" - Booked: " + totalBooked);
        System.out.println(" - Cancelled: " + totalCancelled);
        System.out.printf("Total Clinic Revenue: $%.2f%n", totalRevenue);
        System.out.println("=================================================================\n");
    }
}