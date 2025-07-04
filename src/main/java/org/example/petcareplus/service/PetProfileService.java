package org.example.petcareplus.service;

import org.example.petcareplus.entity.PetProfile;

import java.util.List;

public interface PetProfileService {
    List<PetProfile> findAll();

    PetProfile findById(Integer id);

    void updatePetProfile(Integer id, PetProfile updated);

    PetProfile createEmptyPet();

}
