package org.example.petcareplus.repository;

import org.example.petcareplus.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByServiceCategory(String serviceCategory);

}
