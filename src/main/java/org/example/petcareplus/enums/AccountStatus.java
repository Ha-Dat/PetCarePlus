package org.example.petcareplus.enums;

import java.util.Arrays;
import java.util.List;

public enum AccountStatus {

    ACTIVE("Hoạt động"), BANNED("Khóa"), INACTIVE("Chưa kích hoạt");

    private final String value;

    AccountStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static List<AccountStatus> getAllValues() {
        return Arrays.asList(AccountStatus.values());
    }
}
