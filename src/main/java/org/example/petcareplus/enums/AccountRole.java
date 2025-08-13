package org.example.petcareplus.enums;

import java.util.Arrays;
import java.util.List;

public enum AccountRole {

    SELLER("Nhân viên bán hàng"), ADMIN("Admin"),
    PET_GROOMER("Nhân viên chăm sóc"), VET("Bác sĩ thú y"),
    MANAGER("Quản lý"), CUSTOMER("Khách hàng");

    private final String value;

    AccountRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static List<AccountRole> getRoles() {
        return Arrays.asList(AccountRole.values());
    }

}
