package org.example.petcareplus.service.impl;

import org.example.petcareplus.dto.ProfileDTO;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.City;
import org.example.petcareplus.entity.Profile;
import org.example.petcareplus.repository.CityRepository;
import org.example.petcareplus.repository.ProfileRepository;
import org.example.petcareplus.repository.WardRepository;
import org.example.petcareplus.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final CityRepository cityRepository;

    private final WardRepository wardRepository;

    private final ProfileRepository profileRepository;

    @Autowired
    public ProfileServiceImpl(CityRepository cityRepository, WardRepository wardRepository, ProfileRepository profileRepository) {
        this.cityRepository = cityRepository;
        this.wardRepository = wardRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    public Profile getProfileByAccountAccountId(Long accountId) {
        return profileRepository.findByAccountAccountId(accountId);
    }

    @Override
    public Profile save(Profile profile) {
        return profileRepository.save(profile);
    }

    @Override
    public ProfileDTO getCurrentProfileByAccountAccountId(Long accountId) {
        Profile profile = profileRepository.findByAccountAccountId(accountId);
        if(profile == null) {
            throw new RuntimeException("Không tìn thấy tài khoản");
        }

        ProfileDTO dto = new ProfileDTO();
        dto.setProfileId(profile.getProfileId());
        dto.setName(profile.getAccount().getName());
        dto.setPhone(profile.getAccount().getPhone());
        // City
        if (profile.getCity() != null) {
            dto.setCityId(profile.getCity().getCityId());
            dto.setCityName(profile.getCity().getName());
        } else {
            dto.setCityName("Chọn thành phố");
        }

        // Ward
        if (profile.getWard() != null) {
            dto.setWardId(profile.getWard().getWardId());
            dto.setWardName(profile.getWard().getName());
        } else {
            dto.setWardName("Chọn phường/xã");
        }
        return dto;
    }

    @Override
    public void updateProfile(ProfileDTO profileDTO, Long accountId) {
        Profile profile = profileRepository.findByAccountAccountId(accountId);
        if (profile == null) {
            profile = new Profile();
            // Tùy logic, cần tạo account mới hoặc gán account hiện tại
        }

        Account account = new Account();
        account.setAccountId(accountId); // giả sử account đã tồn tại
        profile.setAccount(account);

        profile.setProfileId(profileDTO.getProfileId());

        if (profile.getAccount() != null) {
            profile.getAccount().setName(profileDTO.getName());
        }

        cityRepository.findById(profileDTO.getCityId()).ifPresent(profile::setCity);
        wardRepository.findById(profileDTO.getWardId()).ifPresent(profile::setWard);

        profileRepository.save(profile);
    }
}
