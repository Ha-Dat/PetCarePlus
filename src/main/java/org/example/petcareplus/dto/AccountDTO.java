package org.example.petcareplus.dto;

import lombok.AllArgsConstructor;
import org.example.petcareplus.entity.Account;

@AllArgsConstructor
public class AccountDTO {
    public Long accountId;
    public String name;
    public String phone;
    public String role;
    public String status;

    public AccountDTO() {
    }

    public AccountDTO(Account a) {
        this.accountId = a.getAccountId();
        this.name = a.getName();
        this.phone = a.getPhone();
        this.role = a.getRole();
        this.status = a.getStatus();
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
