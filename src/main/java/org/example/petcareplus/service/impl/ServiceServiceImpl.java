package org.example.petcareplus.service.impl;

import org.example.petcareplus.enums.ServiceCategory;
import org.example.petcareplus.repository.ServiceRepository;
import org.example.petcareplus.service.ServiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Override
    public Page<org.example.petcareplus.entity.Service> getServicesPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("serviceId").ascending());
        return serviceRepository.findAll(pageable);
    }

    @Override
    public org.example.petcareplus.entity.Service saveService(org.example.petcareplus.entity.Service service) {
        return serviceRepository.save(service);
    }

    @Override
    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }

    @Override
    public List<org.example.petcareplus.entity.Service> findAll() {
        return serviceRepository.findAll();
    }
}
