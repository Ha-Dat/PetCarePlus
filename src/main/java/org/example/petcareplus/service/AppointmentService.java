package org.example.petcareplus.service;

import org.example.petcareplus.entity.AppointmentBooking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface AppointmentService {
    Page<AppointmentBooking> findAll(Pageable pageable);

    Page<AppointmentBooking> getApprovedAppointments(Pageable pageable);

    Page<AppointmentBooking> getPendingAppointments(Pageable pageable);

    Page<AppointmentBooking> getHistoryAppointments(Pageable pageable);

    AppointmentBooking save(AppointmentBooking appointment);

    Optional<AppointmentBooking> findById(Long id);
}
