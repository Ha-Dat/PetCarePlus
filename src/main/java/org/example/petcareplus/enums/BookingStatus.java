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

    public static BookingStatus fromValue(String value) {
        for (BookingStatus status : values()) {
            if (status.value.equalsIgnoreCase(value) || status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant " + BookingStatus.class.getCanonicalName() + "." + value);
    }
}