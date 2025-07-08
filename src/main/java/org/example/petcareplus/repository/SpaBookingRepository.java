package org.example.petcareplus.repository;

import org.example.petcareplus.entity.SpaBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SpaBookingRepository extends JpaRepository<SpaBooking, Long> {
}
