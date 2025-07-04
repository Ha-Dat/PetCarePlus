package org.example.petcareplus.repository;

import org.example.petcareplus.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Profile findByAccountAccountId(Long accountId);
    Optional<Profile> findByAccount_AccountId(Integer accountId);
}

