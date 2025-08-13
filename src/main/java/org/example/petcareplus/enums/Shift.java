package org.example.petcareplus.enums;

public enum Shift {
    MORNING("Ca sáng"), AFTERNOON("Ca chiều"), EVENING("Ca tối");

    private final String value;

    Shift(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
