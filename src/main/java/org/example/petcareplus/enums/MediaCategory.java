package org.example.petcareplus.enums;

public enum MediaCategory {
    POST_VIDEO("Post Video"), PRODUCT_VIDEO("Product Video"), PRODUCT_IMAGE("Product Image"), POST_IMAGE("Post Image"), PROMOTION_IMAGE("Promotion Image"), PET_IMAGE("Pet Image");

    private final String value;

    MediaCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
