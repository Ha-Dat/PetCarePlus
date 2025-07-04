package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.City;
import org.example.petcareplus.entity.District;
import org.example.petcareplus.entity.Ward;
import org.example.petcareplus.repository.CityRepository;
import org.example.petcareplus.repository.DistrictRepository;
import org.example.petcareplus.repository.WardRepository;
import org.example.petcareplus.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private WardRepository wardRepository;

    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    public List<District> getDistrictsByCityId(Long cityId) {
        return districtRepository.findByCity_CityId(cityId);
    }

    public List<Ward> getWardsByDistrictId(Long districtId) {
        return wardRepository.findByDistrict_DistrictId(districtId);
    }

}
