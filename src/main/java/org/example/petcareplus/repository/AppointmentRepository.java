package org.example.petcareplus.repository;
import org.example.petcareplus.dto.MyServiceDTO;
import org.example.petcareplus.entity.AppointmentBooking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentBooking, Long> {
    Page<AppointmentBooking> findAll(Pageable pageable);

    Page<AppointmentBooking> findByStatusIgnoreCase(String status, Pageable pageable);

    @Query("""
        SELECT new org.example.petcareplus.dto.MyServiceDTO(
            p.name, s.name, s.serviceCategory, a.bookDate, a.status
        )
        FROM AppointmentBooking a
        JOIN a.petProfile p
        JOIN p.profile pf
        JOIN pf.account acc
        JOIN a.service s
        WHERE acc.accountId = :accountId
    """)
    List<MyServiceDTO> findAppointmentBookingsByAccountId(@Param("accountId") Long accountId);

}
