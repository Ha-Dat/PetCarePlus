package org.example.petcareplus.repository;

import org.example.petcareplus.entity.HotelBooking;
import org.example.petcareplus.entity.SpaBooking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;


@Repository
public interface SpaBookingRepository extends JpaRepository<SpaBooking, Long> {
    @Query("SELECT h FROM HotelBooking h WHERE h.bookDate >= :start AND h.bookDate < :end")
    Page<SpaBooking> findByBookDateBetween(@Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end,
                                             Pageable pageable);
}
