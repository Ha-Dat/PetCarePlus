package org.example.petcareplus.repository;

import org.example.petcareplus.enums.ServiceCategory;
import org.example.petcareplus.enums.ServiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ServiceRepository extends JpaRepository<org.example.petcareplus.entity.Service, Long> {

    List<org.example.petcareplus.entity.Service> findByServiceCategory(ServiceCategory serviceCategory);

    Page<org.example.petcareplus.entity.Service> findAll(Pageable pageable);
    
    List<org.example.petcareplus.entity.Service> findByStatus(ServiceStatus status);
    
    List<org.example.petcareplus.entity.Service> findByServiceCategoryAndStatus(ServiceCategory serviceCategory, ServiceStatus status);

}
