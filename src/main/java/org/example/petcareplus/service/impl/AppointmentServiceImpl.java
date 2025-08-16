package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.AppointmentBooking;
import org.example.petcareplus.enums.BookingStatus;
import org.example.petcareplus.repository.AppointmentRepository;
import org.example.petcareplus.repository.ServiceRepository;
import org.example.petcareplus.service.AppointmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final ServiceRepository serviceRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, ServiceRepository serviceRepository) {
        this.appointmentRepository = appointmentRepository;
        this.serviceRepository = serviceRepository;
    }

    @Override
    public Page<AppointmentBooking> findAll(Pageable pageable) {
        return appointmentRepository.findAll(pageable);
    }

    @Override
    public Page<AppointmentBooking> getApprovedAppointments(Pageable pageable) {
        return appointmentRepository.findByStatus(BookingStatus.ACCEPTED, pageable);
    }
    @Override
    public Page<AppointmentBooking> getPendingAppointments(Pageable pageable) {
        return appointmentRepository.findByStatus(BookingStatus.PENDING, pageable);
    }
    @Override
    public Page<AppointmentBooking> getHistoryAppointments(Pageable pageable) {
        return appointmentRepository.findByStatus(BookingStatus.REJECTED, pageable);
    }

    @Override
    public AppointmentBooking save(AppointmentBooking appointment){
        return appointmentRepository.save(appointment);
    };

    @Override
    public Optional<AppointmentBooking> findById(Long id){
      return appointmentRepository.findById(id);
    }

    @Override
    public Optional<org.example.petcareplus.entity.Service> Service_findById(Long id) {
        return serviceRepository.findById(id);
    }
}
