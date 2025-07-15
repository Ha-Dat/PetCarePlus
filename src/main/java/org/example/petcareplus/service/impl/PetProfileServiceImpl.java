package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.PetProfile;
import org.example.petcareplus.repository.PetProfileRepository;
import org.example.petcareplus.service.PetProfileService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PetProfileServiceImpl implements PetProfileService {

    private final PetProfileRepository petProfileRepository;
    private Long nextId = 1L;

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

//    @Override
//    // ✅ KHỞI TẠO DỮ LIỆU MẪU KHÔNG DÙNG NULL
//    public PetProfileServiceImpl() {
//        PetProfile pet1 = new PetProfile();
//        pet1.setPetProfileId(nextId++);
//        pet1.setName("Bin");
//        pet1.setSpecies("Chó");
//        pet1.setBreeds("Bulldog");
//        pet1.setAge(8);
//        pet1.setWeight(10.0f);
//
//        PetProfile pet2 = new PetProfile();
//        pet2.setPetProfileId(nextId++);
//        pet2.setName("Milo");
//        pet2.setSpecies("Mèo");
//        pet2.setBreeds("Munchkin");
//        pet2.setAge(3);
//        pet2.setWeight(3.5f);
//
//        petProfiles.add(pet1);
//        petProfiles.add(pet2);
//    }
}
