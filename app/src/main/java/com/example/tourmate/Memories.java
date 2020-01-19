package com.example.tourmate;

public class Memories {
    String TripName, imageId, imageLink;

    public Memories() {
    }

    public Memories(String tripName, String imageId, String imageLink) {
        TripName = tripName;
        this.imageId = imageId;
        this.imageLink = imageLink;
    }

    public String getTripName() {
        return TripName;
    }

    public String getImageId() {
        return imageId;
    }

    public String getImageLink() {
        return imageLink;
    }
}
