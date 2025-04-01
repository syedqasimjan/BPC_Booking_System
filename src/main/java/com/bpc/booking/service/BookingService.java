package com.bpc.booking.service;

import com.bpc.booking.model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookingService {
    private final Clinic clinic;

    public BookingService(Clinic clinic) {
        this.clinic = clinic;
    }

    public void bookByExpertise(String expertise, String patientId) {
        Patient patient = clinic.getPatients().stream()
                .filter(p -> p.getId().equals(patientId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + patientId));

        List<Physiotherapist> physios = clinic.searchByExpertise(expertise);
        if (physios.isEmpty()) {
            throw new IllegalArgumentException("No physiotherapist found with expertise: " + expertise);
        }

        Physiotherapist physio = physios.get(0); // Select the first available physiotherapist
        Treatment treatment = physio.getTreatments().stream()
                .filter(t -> t.getExpertise().equals(expertise))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No treatment found for expertise: " + expertise));

        // Find the earliest available slot
        String dateTime = findEarliestAvailableSlot(physio, treatment);
        if (dateTime == null) {
            throw new IllegalArgumentException("No available slots for the selected physiotherapist.");
        }

        clinic.bookAppointment(patient, physio, treatment, dateTime);
    }

    public void bookByPhysioName(String physioName, String patientId) {
        Patient patient = clinic.getPatients().stream()
                .filter(p -> p.getId().equals(patientId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + patientId));

        Physiotherapist physio = clinic.searchByPhysioName(physioName);
        if (physio == null) {
            throw new IllegalArgumentException("Physiotherapist not found: " + physioName);
        }

        Treatment treatment = physio.getTreatments().get(0); // Select the first treatment
        String dateTime = findEarliestAvailableSlot(physio, treatment);
        if (dateTime == null) {
            throw new IllegalArgumentException("No available slots for the selected physiotherapist.");
        }

        clinic.bookAppointment(patient, physio, treatment, dateTime);
    }

    private String findEarliestAvailableSlot(Physiotherapist physio, Treatment treatment) {
        LocalDateTime startDate = LocalDateTime.of(2025, 4, 1, 9, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 4, 28, 17, 0);
        LocalDateTime current = startDate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        while (current.isBefore(endDate)) {
            if (current.getDayOfWeek().getValue() <= 5) { // Monday-Friday
                if (current.getHour() >= 9 && current.getHour() < 17) { // 9 AM-5 PM
                    String dateTime = current.format(formatter);
                    LocalDateTime appointmentDateTime = LocalDateTime.parse(dateTime, formatter);
                    LocalDateTime appointmentEnd = appointmentDateTime.plusMinutes(treatment.getDuration());

                    // Check availability within physiotherapist's schedule
                    boolean isAvailable = clinic.getAvailabilities().stream()
                            .filter(a -> a.getPhysioId().equals(physio.getId()))
                            .filter(a -> a.getDate().equals(appointmentDateTime.toLocalDate()))
                            .anyMatch(a -> !appointmentDateTime.toLocalTime().isBefore(a.getStartTime()) &&
                                    !appointmentEnd.toLocalTime().isAfter(a.getEndTime()));

                    // Check for overlapping appointments
                    boolean hasOverlap = clinic.getAppointments().stream()
                            .filter(a -> a.getPhysiotherapist().equals(physio))
                            .filter(a -> a.getStatus() != Status.CANCELLED) // Ignore cancelled appointments
                            .anyMatch(a -> {
                                LocalDateTime existingStart = LocalDateTime.parse(a.getDateTime(), formatter);
                                LocalDateTime existingEnd = existingStart.plusMinutes(a.getTreatment().getDuration());
                                return !(appointmentEnd.isBefore(existingStart) || appointmentDateTime.isAfter(existingEnd));
                            });

                    if (isAvailable && !hasOverlap) {
                        return dateTime;
                    }
                }
                current = current.plusHours(1);
            } else {
                current = current.plusDays(1).withHour(9).withMinute(0);
            }
        }
        return null;
    }
}