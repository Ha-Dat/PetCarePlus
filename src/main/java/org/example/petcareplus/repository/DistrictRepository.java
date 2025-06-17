package org.example.petcareplus.repository;

import org.example.petcareplus.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DistrictRepository extends JpaRepository<District, Integer> {
    List<District> findByCity_CityId(Integer cityId);
}
