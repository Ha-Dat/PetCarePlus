package org.example.petcareplus.service.impl;

import org.example.petcareplus.enums.ServiceCategory;
import org.example.petcareplus.repository.ServiceRepository;
import org.example.petcareplus.service.ServiceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;

    public ServiceServiceImpl(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public Optional<org.example.petcareplus.entity.Service> findById(Long id) {
        return serviceRepository.findById(id);
    }

    @Override
    public List<org.example.petcareplus.entity.Service> findByServiceCategory(ServiceCategory serviceCategory) {
        return serviceRepository.findByServiceCategory(serviceCategory);
    }
}
