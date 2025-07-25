package org.example.petcareplus.enums;

public enum AccountRole {

    ADMIN("Admin"), SELLER("Nhân viên bán hàng"),
    PET_GROOMER("Nhân viên chăm sóc"), VET("Bác sĩ thú y"),
    MANAGER("Quản lý"), CUSTOMER("Khách hàng");

    private final String value;

    AccountRole(String value) {
        this.value = value;
    }
}
