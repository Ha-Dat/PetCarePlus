package org.example.petcareplus.controller;

import org.example.petcareplus.entity.District;
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

    @GetMapping("/districts")
    public List<District> getDistricts(@RequestParam("cityId") Integer cityId) {
        return locationService.getDistrictsByCityId(cityId);
    }

    @GetMapping("/wards")
    public List<Ward> getWards(@RequestParam("districtId") Integer districtId) {
        return locationService.getWardsByDistrictId(districtId);
    }
}
