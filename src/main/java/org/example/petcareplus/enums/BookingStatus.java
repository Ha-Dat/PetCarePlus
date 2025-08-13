package org.example.petcareplus.enums;

public enum BookingStatus {
    PENDING("Chờ duyệt"), ACCEPTED("Đã duyệt"), REJECTED("Từ chối"), CANCELLED("Hủy");

    private final String value;

    BookingStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}