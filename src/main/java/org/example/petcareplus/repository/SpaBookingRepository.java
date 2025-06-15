package org.example.petcareplus.repository;

import org.example.petcareplus.entity.SpaBooking;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SpaBookingRepository extends JpaRepository<SpaBooking, Integer> {

}
