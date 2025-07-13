package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.Account;
import org.example.petcareplus.repository.AccountRepository;
import org.example.petcareplus.service.AccountService;
import org.example.petcareplus.util.PasswordHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

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
    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }

}
