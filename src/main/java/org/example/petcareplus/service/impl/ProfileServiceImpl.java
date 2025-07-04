package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.Profile;
import org.example.petcareplus.repository.ProfileRepository;
import org.example.petcareplus.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileServiceImpl implements ProfileService {


    private final ProfileRepository profileRepository;

    @Autowired
    public ProfileServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Profile getProfileByAccountAccountId(Long accountId) {
        return profileRepository.findByAccountAccountId(accountId);
    }


    @Override
    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    @Override
    public Optional<Profile> getProfileById(Integer id) {
        return Optional.empty();
    }

    @Override
    public Optional<Profile> getProfileById(Long id) {
        return profileRepository.findById(id);
    }

    @Override
    public Profile save(Profile profile) {
        return profileRepository.save(profile);
    }
}
