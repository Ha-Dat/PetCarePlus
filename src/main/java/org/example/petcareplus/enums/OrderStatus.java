package org.example.petcareplus.enums;

public enum OrderStatus {

    PENDING("Đang chờ"), COMPLETED("Đã hoàn thành"), PROCESSING("Đang xử lý"),
    DELIVERING("Đang giao"), CANCELLED("Đã hủy"), REJECTED("Từ chối"), APPROVED("Duyệt");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
