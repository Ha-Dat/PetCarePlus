package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.Profile;
import org.example.petcareplus.enums.AccountRole;
import org.example.petcareplus.repository.AccountRepository;
import org.example.petcareplus.repository.ProfileRepository;
import org.example.petcareplus.service.AccountService;
import org.example.petcareplus.util.PasswordHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public boolean isPhoneExists(String phone) {
        return accountRepository.existsByPhone(phone);
    }

    @Override
    public void save(Account account) {
        accountRepository.save(account);
    }

    @Override
    public Optional<Account> findByPhone(String phone) {
        return Optional.empty();
    }

    @Override
    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public Optional<Account> login(String phone, String rawPassword) {
        Optional<Account> optionalAccount = accountRepository.findByPhone(phone);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            String hashedInput = PasswordHasher.hash(rawPassword);
            if (hashedInput.equals(account.getPassword())) {
                return Optional.of(account);
            }
        }
        return Optional.empty();
    }

    @Override
    public Profile getProfileByAccountAccountId(Long accountId) {
        return profileRepository.findByAccountAccountId(accountId);
    }

    @Override
    public Profile profileSave(Profile profile) {
        return profileRepository.save(profile);
    }

    @Override
    public List<Account> getAllAccount() {
        return accountRepository.findAll();
    }

    @Override
    public Optional<Account> getById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public void updateAccount(Account account) {
        accountRepository.save(account);
    }

    @Override
    public Page<Account> getAccountPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("accountId").ascending());
        return accountRepository.findAll(pageable);
    }

    @Override
    public Long countByRole(AccountRole role) {
        return accountRepository.countByRole(role);
    }
}
