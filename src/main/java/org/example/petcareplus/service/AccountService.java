package org.example.petcareplus.service;

import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.Profile;
import org.example.petcareplus.enums.AccountRole;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface AccountService {

    boolean isPhoneExists(String phone);

    void save(Account account);

    Optional<Account> findByPhone(String phone);

    Optional<Account> findById(Long id);

    Optional<Account> login(String phone, String rawPassword);

    Profile getProfileByAccountAccountId(Long accountId);

    Profile profileSave(Profile profile);

    List<Account> getAllAccount();

    Optional<Account> getById(Long id);

    void updateAccount(Account account);

    Page<Account> getAccountPage(int page, int size);

    Long countByRole(AccountRole role);
    
    List<Account> findByRoleIn(List<AccountRole> roles);
}
