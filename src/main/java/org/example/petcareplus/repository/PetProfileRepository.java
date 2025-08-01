package org.example.petcareplus.repository;

import org.example.petcareplus.entity.PetProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetProfileRepository extends JpaRepository<PetProfile, Long> {

}
