package com.ozrahat.healthai.models;

public enum Units {
    CM(0,"cm"),
    INCH(1,"inch"),
    KG(2,"kg"),
    LBS(3,"lbs");

    public final int id;
    public final String label;

    Units(Integer id, String label) {
        this.id = id;
        this.label = label;
    }
}
