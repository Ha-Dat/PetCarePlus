package org.example.petcareplus.enums;

public enum ScheduleStatus {
    PENDING("Chưa đến"), PRESENT("Có mặt"), ABSENT("Vắng mặt"), LEAVE_APPROVED("Nghỉ có phép");

    private final String value;

    ScheduleStatus(String value) {
        this.value = value;
    }
}
