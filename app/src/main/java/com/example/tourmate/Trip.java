package com.example.tourmate;

public class Trip {
    String name, description,startDate, endDate, budget, tripId, tripExpence;

    public Trip() {
    }

    public Trip(String name, String description, String startDate, String endDate, String budget, String tripId, String tripExpence) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
        this.tripId = tripId;
        this.tripExpence = tripExpence;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getBudget() {
        return budget;
    }

    public String getTripId() {
        return tripId;
    }

    public String getTripExpence() {
        return tripExpence;
    }
}
