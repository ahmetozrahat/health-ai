package com.ozrahat.healthai.models;

public enum PlaceType {
    HOSPITAL("hospital"),
    PHARMACY("pharmacy");

    public final String id;

    PlaceType(String id) {
        this.id = id;
    }
}
