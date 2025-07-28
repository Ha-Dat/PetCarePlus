package org.example.petcareplus.repository;

import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.PetProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetProfileRepository extends JpaRepository<PetProfile, Long> {
    List<PetProfile> findByProfileAccount(Account account);
}
