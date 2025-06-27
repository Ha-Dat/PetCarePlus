package org.example.petcareplus.service;

import org.example.petcareplus.entity.PetProfile;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PetProfileService {

    private final List<PetProfile> petProfiles = new ArrayList<>();
    private int nextId = 1;

    public List<PetProfile> findAll() {
        return petProfiles;
    }

    public PetProfile findById(Integer id) {
        return petProfiles.stream()
                .filter(p -> p.getPetProfileId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void updatePetProfile(Integer id, PetProfile updated) {
        PetProfile existing = findById(id);
        if (existing != null) {
            existing.setName(updated.getName());
            existing.setAge(updated.getAge());
            existing.setSpecies(updated.getSpecies());
            existing.setBreeds(updated.getBreeds());
            existing.setWeight(updated.getWeight());
        }
    }

    public PetProfile createEmptyPet() {
        PetProfile pet = new PetProfile();
        pet.setPetProfileId(nextId++);
        pet.setName("Thú cưng mới");
        pet.setAge(0);
        pet.setSpecies("");
        pet.setBreeds("");
        pet.setWeight(0.0f);
        petProfiles.add(pet);
        return pet;
    }

    // ✅ KHỞI TẠO DỮ LIỆU MẪU KHÔNG DÙNG NULL
    public PetProfileService() {
        PetProfile pet1 = new PetProfile();
        pet1.setPetProfileId(nextId++);
        pet1.setName("Bin");
        pet1.setSpecies("Chó");
        pet1.setBreeds("Bulldog");
        pet1.setAge(8);
        pet1.setWeight(10.0f);

        PetProfile pet2 = new PetProfile();
        pet2.setPetProfileId(nextId++);
        pet2.setName("Milo");
        pet2.setSpecies("Mèo");
        pet2.setBreeds("Munchkin");
        pet2.setAge(3);
        pet2.setWeight(3.5f);

        petProfiles.add(pet1);
        petProfiles.add(pet2);
    }
}
