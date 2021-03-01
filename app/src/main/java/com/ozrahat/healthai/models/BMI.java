package com.ozrahat.healthai.models;

/**
 * This enum class is for classification of obesity.
 * @see <a href="https://en.wikipedia.org/wiki/Classification_of_obesity">Reference</a>
 *
 * @author ahmetozrahat25
 * @since 2021-03-01
 * @version 1.0.0
 */
public enum BMI {
    UNDER_WEIGHT(0, "underweight"),
    NORMAL_WEIGHT(1, "normal weight"),
    OVER_WEIGHT(3, "overweight"),
    CLASS1_OBESITY(4, "class 1 obesity"),
    CLASS2_OBESITY(5, "class 2 obesity"),
    CLASS3_OBESITY(6, "class 3 obesity");

    public final int id;
    public final String label;

    BMI(Integer id, String label) {
        this.id = id;
        this.label = label;
    }
}
