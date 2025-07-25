package org.example.petcareplus.enums;

public enum ServiceCategory {

    SPA("Spa"), HOTEL("Khách sạn thú cưng"), APPOINTMENT("Khám bệnh");

    private final String value;

    ServiceCategory(String value) {
        this.value = value;
    }
}
