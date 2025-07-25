package org.example.petcareplus.repository;
import org.example.petcareplus.entity.AppointmentBooking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentBooking, Long> {
    Page<AppointmentBooking> findAll(Pageable pageable);

    Page<AppointmentBooking> findByStatusIgnoreCase(String status, Pageable pageable);
}
