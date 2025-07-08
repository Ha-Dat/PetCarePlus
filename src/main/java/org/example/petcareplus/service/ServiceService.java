package org.example.petcareplus.service;

import org.example.petcareplus.entity.Service;

import java.util.List;
import java.util.Optional;

public interface ServiceService {

    Optional<Service> findById(Long id);

    List<Service> findByServiceCategory(String serviceCategory);

}
