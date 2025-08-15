package org.example.petcareplus.repository;

import org.example.petcareplus.dto.MyServiceDTO;
import org.example.petcareplus.entity.HotelBooking;
import org.example.petcareplus.enums.BookingStatus;
import org.example.petcareplus.enums.ServiceCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
                        SELECT h FROM HotelBooking h
                        JOIN Service s on h.service.serviceId = s.serviceId
                        JOIN PetProfile p on h.petProfile.petProfileId = p.petProfileId
                        JOIN Profile pr on p.profile.profileId= pr.profileId
                        WHERE pr.profileId = :profileId
            """)
    List<HotelBooking> findByProfileId(Long profileId);

    @Query("""
                        SELECT h FROM HotelBooking h
                        JOIN PetProfile p on h.petProfile.petProfileId = p.petProfileId
                        JOIN Profile pr on p.profile.profileId= pr.profileId
                        WHERE pr.profileId = :profileId and h.status = "PENDING"
            """)
    List<HotelBooking> findByProfileIdAndStatus(Long profileId, BookingStatus status);

    @Query("SELECT h FROM HotelBooking h WHERE h.bookDate >= :start AND h.bookDate < :end")
    Page<HotelBooking> findByBookDateBetween(@Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end,
                                             Pageable pageable);

    @Query("SELECT MONTH(h.bookDate) AS month, COUNT(h) AS total " +
            "FROM HotelBooking h " +
            "WHERE YEAR(h.bookDate) = YEAR(CURRENT_DATE) " +
            "GROUP BY MONTH(h.bookDate) " +
            "ORDER BY month")
    List<Object[]> getBookingCountByMonthInCurrentYear();

    Long countByStatus(BookingStatus status);
}
