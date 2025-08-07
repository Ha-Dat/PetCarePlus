package org.example.petcareplus.repository;

import org.example.petcareplus.dto.MyServiceDTO;
import org.example.petcareplus.entity.HotelBooking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HotelBookingRepository extends JpaRepository<HotelBooking, Long> {
    @EntityGraph(attributePaths = {"petProfile", "service"})
    List<HotelBooking> findAll();

    @Query("""
        SELECT new org.example.petcareplus.dto.MyServiceDTO(
            h.hotelBookingId ,p.name, s.name, s.serviceCategory, h.bookDate, h.status
        )
        FROM HotelBooking h
        JOIN h.petProfile p
        JOIN p.profile pf
        JOIN pf.account acc
        JOIN h.service s
        WHERE acc.accountId = :accountId
    """)
    List<MyServiceDTO> findHotelBookingsByAccountId(@Param("accountId") Long accountId);
    @Query("SELECT h FROM HotelBooking h WHERE h.bookDate >= :start AND h.bookDate < :end")
    Page<HotelBooking> findByBookDateBetween(@Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end,
                                             Pageable pageable);
}
