package org.example.petcareplus.service;

import org.example.petcareplus.entity.AppointmentBooking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface AppointmentService {
    Page<AppointmentBooking> findAll(Pageable pageable);

    Page<AppointmentBooking> getApprovedAppointments(Pageable pageable);

    Page<AppointmentBooking> getPendingAppointments(Pageable pageable);

    Page<AppointmentBooking> getHistoryAppointments(Pageable pageable);
}
