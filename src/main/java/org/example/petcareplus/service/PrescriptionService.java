package org.example.petcareplus.service;

import org.example.petcareplus.entity.Prescription;

import java.util.List;

public interface PrescriptionService {
    Prescription save(Prescription prescription);
    Prescription findByAppointmentId(Long appointmentId);
    List<Prescription> findAll();
}

