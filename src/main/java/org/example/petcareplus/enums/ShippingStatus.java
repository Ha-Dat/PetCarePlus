package org.example.petcareplus.enums;

public enum ShippingStatus {
    DELIVERING("Đang giao"), DELIVERED("Đã giao"), CANCELLED("Huỷ");

    private final String value;

    ShippingStatus(String value) {
        this.value = value;
    }
}
