package org.example.petcareplus.enums;

public enum AccountStatus {

    ACTIVE("Hoạt động"), BANNED("Khóa");

    private final String value;

    AccountStatus(String value) {
        this.value = value;
    }
}
