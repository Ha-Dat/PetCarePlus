package org.example.petcareplus.service;

import org.example.petcareplus.dto.ProfileDTO;
import org.example.petcareplus.entity.City;
import org.example.petcareplus.entity.Profile;
import java.util.List;
import java.util.Optional;


public interface ProfileService {

    Profile getProfileByAccountAccountId(Long accountId);

    Profile save(Profile profile);

    ProfileDTO getCurrentProfileByAccountAccountId(Long accountId);

    void updateProfile(ProfileDTO profileDTO, Long accountId);
}




