package org.example.petcareplus.repository;

import org.example.petcareplus.entity.Service;
import org.example.petcareplus.enums.ServiceCategory;
import org.example.petcareplus.enums.ServiceCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByServiceCategory(ServiceCategory serviceCategory);

    Page<Service> findAll(Pageable pageable);

}
