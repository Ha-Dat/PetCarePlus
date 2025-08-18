package org.example.petcareplus.repository;

import org.example.petcareplus.entity.Account;
import org.example.petcareplus.enums.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByPhone(String phone);

    boolean existsByPhone(String phone);

    Long countByRole(AccountRole role);
    
    List<Account> findByRoleIn(List<AccountRole> roles);
}
