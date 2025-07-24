package org.example.petcareplus.service;

import org.example.petcareplus.dto.ProfileDTO;
import org.example.petcareplus.entity.City;
import org.example.petcareplus.entity.Profile;
import java.util.List;
import java.util.Optional;


public interface ProfileService {

    Profile getProfileByAccountAccountId(Long accountId);

    List<Profile> getAllProfiles();

    Optional<Profile> getProfileById(Integer id);

    Optional<Profile> getProfileById(Long id);

    Optional<City> findCityById(Long cityId);

    Profile save(Profile profile);

    List<City> getAllCities();

    ProfileDTO getCurrentProfileByAccountAccountId(Long accountId);

    void updateProfile(ProfileDTO profileDTO, Long accountId);
}




