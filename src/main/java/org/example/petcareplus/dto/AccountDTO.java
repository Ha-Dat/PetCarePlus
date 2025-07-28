package org.example.petcareplus.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.enums.AccountRole;
import org.example.petcareplus.enums.AccountStatus;

@AllArgsConstructor
public class AccountDTO {
    public Long accountId;
    public String name;
    public String phone;
    public AccountRole role;
    public AccountStatus status;

    public AccountDTO() {
    }

    public AccountDTO(Account account) {
        this.accountId = account.getAccountId();
        this.name = account.getName();
        this.phone = account.getPhone();
        this.role = account.getRole();
        this.status = account.getStatus();
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

    public AccountRole getRole() {
        return role;
    }

    public void setRole(AccountRole role) {
        this.role = role;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}
