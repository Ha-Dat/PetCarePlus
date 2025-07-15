package org.example.petcareplus.service;

import org.example.petcareplus.entity.PetProfile;

import java.util.List;

public interface PetProfileService {
    List<PetProfile> findAll();

    PetProfile findById(Long id);

    void updatePetProfile(Long id, PetProfile updated);

    PetProfile createEmptyPet();

    PetProfile save(PetProfile petProfile);

}
