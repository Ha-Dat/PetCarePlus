package org.example.petcareplus.repository;

import org.example.petcareplus.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    Optional<Prescription> findByAppointment_AppointmentBookingId(Long appointmentBookingId);
    List<Prescription> findAllByAppointment_AppointmentBookingId(Long appointmentBookingId);
}
