package io.github.asmolenkov.tennismatchscoreboard.model;

public enum Point {
    ZERO("00"),
    FIFTEEN("15"),
    THIRTY("30"),
    FORTY("40"),
    ADVANTAGE("AD");

    private final String displayValue;

    Point(String displayValue) {
        this.displayValue = displayValue;
    }

    // Доменная модель не должна знать то, как она отображается во View — это нарушает Принцип единой ответственности (SRP).
        // В идеале эта логика должна быть в маппере.
    public String getDisplayValue() {
        return displayValue;
    }




}
