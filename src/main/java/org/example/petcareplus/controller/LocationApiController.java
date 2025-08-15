package org.example.petcareplus.controller;

import org.example.petcareplus.entity.City;
import org.example.petcareplus.entity.Ward;
import org.example.petcareplus.service.LocationService;
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
    public List<Ward> getDistricts(@PathVariable Long cityId) {
        return locationService.getWardsByCityId(cityId);
    }

}
