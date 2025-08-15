package org.example.petcareplus.enums;

public enum ScheduleRequestStatus {
    PENDING("Chờ duyệt"), APPROVED("Chấp nhận"), REJECTED("Từ chối");

    private final String value;

    ScheduleRequestStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
