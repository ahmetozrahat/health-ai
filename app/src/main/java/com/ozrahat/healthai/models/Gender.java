package com.ozrahat.healthai.models;

public enum Gender {
    MALE(0,"Male"),
    FEMALE(1,"Female");

    public final int id;
    public final String label;

    Gender(Integer id, String label) {
        this.id = id;
        this.label = label;
    }
}
