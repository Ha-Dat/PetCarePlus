package org.example.petcareplus.repository;

import org.example.petcareplus.dto.MyServiceDTO;
import org.example.petcareplus.entity.HotelBooking;
import org.example.petcareplus.enums.BookingStatus;
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
        WHERE s.serviceCategory = 'HOTEL' AND acc.accountId = :accountId
    """)
    List<MyServiceDTO> findHotelBookingsByAccountId(@Param("accountId") Long accountId);
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

    // Kiểm tra xem có booking nào đang sử dụng service không
    boolean existsByServiceServiceId(Long serviceId);

    // Tìm tất cả booking theo petProfileId
    List<HotelBooking> findByPetProfile_petProfileId(Long petProfileId);
}
