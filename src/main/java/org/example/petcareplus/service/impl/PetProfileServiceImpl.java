package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.PetProfile;
import org.example.petcareplus.repository.PetProfileRepository;
import org.example.petcareplus.service.PetProfileService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PetProfileServiceImpl implements PetProfileService {

    private final PetProfileRepository petProfileRepository;

    public PetProfileServiceImpl(PetProfileRepository petProfileRepository) {
        this.petProfileRepository = petProfileRepository;
    }

    @Override
    public List<PetProfile> findAll() {
        return petProfileRepository.findAll();
    }

    @Override
    public PetProfile findById(Long id) {
        return petProfileRepository.findById(Long.valueOf(id)).orElse(null);
    }

    @Override
    public void updatePetProfile(Long id, PetProfile updated) {
        PetProfile existing = findById(id);
        if (existing != null) {
            existing.setName(updated.getName());
            existing.setAge(updated.getAge());
            existing.setSpecies(updated.getSpecies());
            existing.setBreeds(updated.getBreeds());
            existing.setWeight(updated.getWeight());
            petProfileRepository.save(existing);
        }
    }

    @Override
    public PetProfile createEmptyPet() {
        PetProfile pet = new PetProfile();
        pet.setName("Thú cưng mới");
        pet.setAge(0);
        pet.setSpecies("");
        pet.setBreeds("");
        pet.setWeight(0.0f);
        return petProfileRepository.save(pet);
    }

    @Override
    public PetProfile save(PetProfile petProfile) {
        return petProfileRepository.save(petProfile);
    }

    @Override
    public List<PetProfile> findByProfileId(Long profileId) {
        return petProfileRepository.findAllByProfile_ProfileId(profileId);
    }
}
