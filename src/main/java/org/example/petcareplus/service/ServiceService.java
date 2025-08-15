package org.example.petcareplus.service;

import org.example.petcareplus.entity.Service;
import org.example.petcareplus.enums.ServiceCategory;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ServiceService {

    Optional<Service> findById(Long id);

    List<Service> findByServiceCategory(ServiceCategory serviceCategory);

    Page<Service> getServicesPaginated(int page, int size);

    Service saveService(Service service);

    void deleteService(Long id);

    List<Service> findAll();
}
