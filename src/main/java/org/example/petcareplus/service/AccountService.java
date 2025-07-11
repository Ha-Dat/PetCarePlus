package org.example.petcareplus.service;

import org.example.petcareplus.dto.RegisterDTO;
import org.example.petcareplus.entity.Account;

import java.util.Optional;

public interface AccountService {
    boolean isPhoneExists(String phone);
    void save(Account account);
    Optional<Account> findByPhone(String phone);
    Optional<Account> login(String phone, String rawPassword);

}
