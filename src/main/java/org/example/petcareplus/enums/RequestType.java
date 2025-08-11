package org.example.petcareplus.enums;

public enum RequestType {
    CHANGE("Đổi lịch"), OFF("Xin nghỉ");

    private final String value;

    RequestType(String value) {
        this.value = value;
    }
}
