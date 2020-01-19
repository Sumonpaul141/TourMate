package com.example.tourmate;

public class Expence {
    String expenceId, expenceName, expenceAmount, tripId;

    public Expence() {
    }

    public Expence(String expenceId, String expenceName, String expenceAmount, String tripId) {
        this.expenceId = expenceId;
        this.expenceName = expenceName;
        this.expenceAmount = expenceAmount;
        this.tripId = tripId;
    }

    public String getExpenceId() {
        return expenceId;
    }

    public String getExpenceName() {
        return expenceName;
    }

    public String getExpenceAmount() {
        return expenceAmount;
    }

    public String getTripId() {
        return tripId;
    }
}
