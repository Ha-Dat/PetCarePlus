package org.example.petcareplus.service;

import org.example.petcareplus.dto.PrescriptionDTO;
import org.example.petcareplus.entity.Prescription;

import java.util.List;

public interface PrescriptionService {
    Prescription save(Prescription prescription);
    Prescription findByAppointmentId(Long appointmentId);
    List<Prescription> findAll();
    List<PrescriptionDTO> getPrescriptionsByAppointmentId(Long appointmentId);
    PrescriptionDTO createPrescription(PrescriptionDTO prescriptionDTO);
    PrescriptionDTO updatePrescription(PrescriptionDTO prescriptionDTO);
    void deletePrescription(Long prescriptionId);
}

