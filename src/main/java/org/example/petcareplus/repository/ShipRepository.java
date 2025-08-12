package org.example.petcareplus.repository;

import org.example.petcareplus.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipRepository extends JpaRepository<Shipment, Long> {
}
