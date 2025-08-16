package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.Prescription;
import org.example.petcareplus.repository.PrescriptionRepository;
import org.example.petcareplus.service.PrescriptionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {
    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionServiceImpl(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    @Override
    public Prescription save(Prescription prescription) {
        return  prescriptionRepository.save(prescription);
    }

    @Override
    public Prescription findByAppointmentId(Long appointmentId) {
        return prescriptionRepository.findByAppointment_AppointmentBookingId(appointmentId).orElse(null);
    }

    @Override
    public List<Prescription> findAll() {
        return prescriptionRepository.findAll();
    }
}

