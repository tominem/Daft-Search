package com.danielbyrne.daftsearch.domain;

public enum ModeOfTransport {

    DRIVING("Driving"),
    WALKING("Walking"),
    BICYCLING("Cycling"),
    TRANSIT("Public Transport");

    private final String displayName;

    ModeOfTransport(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}