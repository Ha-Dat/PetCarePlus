package org.example.petcareplus.repository;

import org.example.petcareplus.entity.HotelBooking;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelBookingRepository extends JpaRepository<HotelBooking, Long> {
    @EntityGraph(attributePaths = {"petProfile", "service"})
    List<HotelBooking> findAll();
}
