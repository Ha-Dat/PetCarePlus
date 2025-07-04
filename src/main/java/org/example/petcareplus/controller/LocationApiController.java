package org.example.petcareplus.controller;

import org.example.petcareplus.entity.District;
import org.example.petcareplus.entity.Ward;
import org.example.petcareplus.service.impl.LocationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/location")
public class LocationApiController {

    @Autowired
    private LocationServiceImpl locationService;

    @GetMapping("/districts")
    public List<District> getDistricts(@RequestParam("cityId") Long cityId) {
        return locationService.getDistrictsByCityId(cityId);
    }

    @GetMapping("/wards")
    public List<Ward> getWards(@RequestParam("districtId") Long districtId) {
        return locationService.getWardsByDistrictId(districtId);
    }
}
