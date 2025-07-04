package org.example.petcareplus.service;

import org.example.petcareplus.entity.City;
import org.example.petcareplus.entity.District;
import org.example.petcareplus.entity.Ward;

import java.util.List;

public interface LocationService {
    public List<City> getAllCities();

    public List<District> getDistrictsByCityId(Long cityId);

    public List<Ward> getWardsByDistrictId(Long districtId);
}
