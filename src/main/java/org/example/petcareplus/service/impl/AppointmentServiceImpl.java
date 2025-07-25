package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.AppointmentBooking;
import org.example.petcareplus.repository.AppointmentRepository;
import org.example.petcareplus.service.AppointmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public Page<AppointmentBooking> findAll(Pageable pageable) {
        return appointmentRepository.findAll(pageable);
    }

    @Override
    public Page<AppointmentBooking> getApprovedAppointments(Pageable pageable) {
        return appointmentRepository.findByStatusIgnoreCase("approved", pageable);
    }
    @Override
    public Page<AppointmentBooking> getPendingAppointments(Pageable pageable) {
        return appointmentRepository.findByStatusIgnoreCase("pending", pageable);
    }
    @Override
    public Page<AppointmentBooking> getHistoryAppointments(Pageable pageable) {
        return appointmentRepository.findByStatusIgnoreCase("done", pageable);
    }

    @Override
    public AppointmentBooking save(AppointmentBooking appointment){
        return appointmentRepository.save(appointment);
    };

    @Override
    public Optional<AppointmentBooking> findById(Long id){
      return appointmentRepository.findById(id);
    };
}
