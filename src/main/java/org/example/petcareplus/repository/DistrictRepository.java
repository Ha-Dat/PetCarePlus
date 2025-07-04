package org.example.petcareplus.repository;

import org.example.petcareplus.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    List<District> findByCity_CityId(Long cityId);
}
