package org.example.petcareplus.repository;

import org.example.petcareplus.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Integer> {
    Optional<Profile> findByAccount_AccountId(Integer accountId);

    Profile findByAccountAccountId(int accountId);

}

