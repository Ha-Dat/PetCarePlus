package org.example.petcareplus.enums;

public enum ServiceStatus {
    ACTIVE("Hoạt động"), BANNED("Khóa");

    private final String value;

    ServiceStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
