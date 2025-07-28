package org.example.petcareplus.repository;

import org.example.petcareplus.dto.MyServiceDTO;
import org.example.petcareplus.entity.SpaBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
        WHERE s.serviceCategory = 'SPA' AND acc.accountId = :accountId
    """)
    List<MyServiceDTO> findSpaBookingsByAccountId(@Param("accountId") Long accountId);

}
