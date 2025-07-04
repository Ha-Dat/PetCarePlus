package org.example.petcareplus.repository;

import org.example.petcareplus.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Integer> {

    List<Service> findByServiceCategory(String serviceCategory);

}
