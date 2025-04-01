package com.bpc.booking.model;

public class Treatment {
    private String name;
    private String expertise;
    private int duration;
    private double cost;

    public Treatment(String name, String expertise, int duration, double cost) {
        this.name = name;
        this.expertise = expertise;
        this.duration = duration;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public String getExpertise() {
        return expertise;
    }

    public int getDuration() {
        return duration;
    }

    public double getCost() {
        return cost;
    }
}