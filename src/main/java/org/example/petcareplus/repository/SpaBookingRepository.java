package org.example.petcareplus.repository;

import org.example.petcareplus.dto.MyServiceDTO;
import org.example.petcareplus.entity.SpaBooking;
import org.example.petcareplus.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import java.util.List;


@Repository
public interface SpaBookingRepository extends JpaRepository<SpaBooking, Long> {

    @Query("""
        SELECT sb FROM SpaBooking sb
        JOIN Service s on sb.service.serviceId = s.serviceId
        JOIN PetProfile p on sb.petProfile.petProfileId = p.petProfileId
        JOIN Profile pr on p.profile.profileId= pr.profileId
        WHERE pr.profileId = :profileId
        """)
    List<SpaBooking> findByProfileId(Long profileId);

    @Query("""
        SELECT sb FROM SpaBooking sb
        JOIN PetProfile p on sb.petProfile.petProfileId = p.petProfileId
        JOIN Profile pr on p.profile.profileId= pr.profileId
        WHERE pr.profileId = :profileId AND sb.status = "PENDING"
        """)
    List<SpaBooking> findByProfileIdAndStatus(Long profileId, BookingStatus status);

    @Query("SELECT h FROM SpaBooking h WHERE h.bookDate >= :start AND h.bookDate < :end")
    Page<SpaBooking> findByBookDateBetween(@Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end,
                                           Pageable pageable);

    @Query("SELECT MONTH(s.bookDate) AS month, COUNT(s) AS total " +
            "FROM SpaBooking s " +
            "WHERE YEAR(s.bookDate) = YEAR(CURRENT_DATE) " +
            "GROUP BY MONTH(s.bookDate) " +
            "ORDER BY month")
    List<Object[]> getBookingCountByMonthInCurrentYear();

    long countByStatus(BookingStatus status);
}
