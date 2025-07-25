package org.example.petcareplus.controller;

import org.example.petcareplus.entity.City;
import org.example.petcareplus.entity.District;
import org.example.petcareplus.entity.Ward;
import org.example.petcareplus.service.LocationService;
import org.example.petcareplus.service.impl.LocationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/location")
public class LocationApiController {

    @Autowired
    private LocationService locationService;

    @GetMapping("/city")
    public List<City> getCities(){
        return locationService.getAllCities();
    }

    @GetMapping("/districts/{cityId}")
    public List<District> getDistricts(@PathVariable Long cityId) {
        return locationService.getDistrictsByCityId(cityId);
    }

    @GetMapping("/wards/{cityId}")
    public List<Ward> getWards(@PathVariable Long districtId) {
        return locationService.getWardsByDistrictId(districtId);
    }
}
