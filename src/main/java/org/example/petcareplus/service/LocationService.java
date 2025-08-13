package org.example.petcareplus.service;

import org.example.petcareplus.entity.City;
import org.example.petcareplus.entity.Ward;

import java.util.List;

public interface LocationService {
    List<City> getAllCities();

    List<Ward> getWardsByCityId(Long cityId);
}
