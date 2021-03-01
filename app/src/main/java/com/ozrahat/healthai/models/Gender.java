package com.ozrahat.healthai.models;

public enum Gender {
    MALE("M","Male"),
    FEMALE("F","Female");

    public final String id;
    public final String label;

    Gender(String id, String label) {
        this.id = id;
        this.label = label;
    }
}
