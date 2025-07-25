package org.example.petcareplus.enums;

public enum PaymentStatus {
    PENDING("Chờ duyệt"), APPROVED("Duyệt"), CANCELLED("Hủy");

    private final String value;

    PaymentStatus(String value) {
        this.value = value;
    }
}
