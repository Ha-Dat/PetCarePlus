package org.example.petcareplus.repository;

import org.example.petcareplus.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Integer> {
}

