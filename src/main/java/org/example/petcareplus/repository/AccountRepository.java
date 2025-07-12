package org.example.petcareplus.repository;

import org.example.petcareplus.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByPhone(String phone);
    boolean existsByPhone(String phone);
}
