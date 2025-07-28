package org.example.petcareplus.service;

import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.PetProfile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PetProfileService {
    List<PetProfile> findByAccount(Account account);

    PetProfile findById(Long id);

    void updatePetProfile(Long id, PetProfile updated);

    PetProfile createEmptyPet();

    PetProfile save(PetProfile petProfile);

    List<PetProfile> findByProfileId(Long profileId);

    void uploadPetImage(Long petProfileId, MultipartFile imageFile);
}
