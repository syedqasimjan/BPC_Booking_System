package com.bpc.booking.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Physiotherapist {
    private final String id;
    private final String name;
    private final String address;
    private final String phoneNumber;
    private final List<String> expertise;
    private final List<Treatment> treatments;

    public Physiotherapist(String id, String name, String address, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.expertise = new ArrayList<>();
        this.treatments = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public List<String> getExpertise() {
        return expertise;
    }

    public void addExpertise(String expertise) {
        this.expertise.add(expertise);
    }

    public List<Treatment> getTreatments() {
        return treatments;
    }

    public void addTreatment(Treatment treatment) {
        this.treatments.add(treatment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Physiotherapist that = (Physiotherapist) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}