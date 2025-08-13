package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.City;
import org.example.petcareplus.entity.Ward;
import org.example.petcareplus.repository.CityRepository;
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
    private WardRepository wardRepository;

    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    public List<Ward> getWardsByCityId(Long cityId) {
        return wardRepository.findByCity_CityId(cityId);
    }

}
