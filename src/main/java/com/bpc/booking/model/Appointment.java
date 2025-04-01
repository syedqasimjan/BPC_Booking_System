package com.bpc.booking.model;

import java.util.Objects;

public class Appointment {
    private Patient patient;
    private Physiotherapist physiotherapist;
    private Treatment treatment;
    private String dateTime;
    private Status status;

    public Appointment(Patient patient, Physiotherapist physiotherapist, Treatment treatment, String dateTime) {
        this.patient = patient;
        this.physiotherapist = physiotherapist;
        this.treatment = treatment;
        this.dateTime = dateTime;
        this.status = Status.BOOKED;
    }

    public Patient getPatient() {
        return patient;
    }

    public Physiotherapist getPhysiotherapist() {
        return physiotherapist;
    }

    public Treatment getTreatment() {
        return treatment;
    }

    public String getDateTime() {
        return dateTime;
    }

    public Status getStatus() {
        return status;
    }

    public void changeStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Appointment that = (Appointment) o;
        return Objects.equals(dateTime, that.dateTime) &&
                Objects.equals(physiotherapist, that.physiotherapist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime, physiotherapist);
    }

    @Override
    public String toString() {
        return patient.getId() + "," + patient.getName() + "," + patient.getAddress() + "," + patient.getPhoneNumber() + "," +
                physiotherapist.getId() + "," + physiotherapist.getName() + "," + treatment.getName() + "," +
                treatment.getExpertise() + "," + treatment.getDuration() + "," + treatment.getCost() + "," +
                dateTime + "," + status + "," + physiotherapist.getPhoneNumber();
    }
}