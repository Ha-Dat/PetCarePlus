package org.example.petcareplus.enums;

public enum ProductStatus {

    IN_STOCK("Còn hàng"), OUT_OF_STOCK("Hết hàng"), INACTIVE("Không hoạt động");

    private final String value;

    ProductStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
