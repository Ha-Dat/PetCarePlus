package org.example.petcareplus.repository;

import org.example.petcareplus.dto.MyServiceDTO;
import org.example.petcareplus.entity.SpaBooking;
import org.example.petcareplus.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import java.util.List;


@Repository
public interface SpaBookingRepository extends JpaRepository<SpaBooking, Long> {

    @Query("""
        SELECT new org.example.petcareplus.dto.MyServiceDTO(
            sp.spaBookingId, p.name, s.name, s.serviceCategory, sp.bookDate, sp.status
        )
        FROM SpaBooking sp
        JOIN sp.petProfile p
        JOIN p.profile pf
        JOIN pf.account acc
        JOIN sp.service s
        WHERE acc.accountId = :accountId
    """)
    List<MyServiceDTO> findSpaBookingsByAccountId(@Param("accountId") Long accountId);

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
