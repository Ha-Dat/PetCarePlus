package org.example.petcareplus.repository;

import org.example.petcareplus.entity.PetProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetProfileRepository extends JpaRepository<PetProfile, Integer> {
}
