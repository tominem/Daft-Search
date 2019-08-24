package com.danielbyrne.daftsearch.domain;

public enum TravelMode {

    DRIVING("Driving"),
    WALKING("Walking"),
    BICYCLING("Cycling"),
    TRANSIT("Public Transport");

    private final String displayName;

    TravelMode(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}