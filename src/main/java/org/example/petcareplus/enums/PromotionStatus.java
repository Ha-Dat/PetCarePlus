package org.example.petcareplus.enums;

public enum PromotionStatus {

    ACTIVE("Hoạt động"), INACTIVE("Hết hạn");

    private final String value;

    PromotionStatus(String value) {
        this.value = value;
    }
}