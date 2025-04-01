package com.bpc.booking.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Availability {
    private String physioId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    public Availability(String physioId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.physioId = physioId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getPhysioId() {
        return physioId;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}