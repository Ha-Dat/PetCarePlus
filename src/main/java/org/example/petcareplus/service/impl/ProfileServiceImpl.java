package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.Profile;
import org.example.petcareplus.repository.ProfileRepository;
import org.example.petcareplus.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl implements ProfileService {


    private final ProfileRepository profileRepository;

    @Autowired
    public ProfileServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Profile getProfileByAccountAccountId(int accountId) {
        return profileRepository.findByAccountAccountId(accountId);
    }
}
