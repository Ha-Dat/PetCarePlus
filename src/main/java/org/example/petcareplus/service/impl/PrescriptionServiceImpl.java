package org.example.petcareplus.service.impl;

import org.example.petcareplus.dto.PrescriptionDTO;
import org.example.petcareplus.entity.AppointmentBooking;
import org.example.petcareplus.entity.Prescription;
import org.example.petcareplus.repository.AppointmentRepository;
import org.example.petcareplus.repository.PrescriptionRepository;
import org.example.petcareplus.service.PrescriptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {
    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;

    public PrescriptionServiceImpl(PrescriptionRepository prescriptionRepository, AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
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

    @Override
    public List<PrescriptionDTO> getPrescriptionsByAppointmentId(Long appointmentId) {
        return prescriptionRepository.findAllByAppointment_AppointmentBookingId(appointmentId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PrescriptionDTO createPrescription(PrescriptionDTO prescriptionDTO) {
        // Validate input
        if (prescriptionDTO.getAppointmentBookingId() == null) {
            throw new IllegalArgumentException("Appointment ID is required");
        }

        AppointmentBooking appointment = appointmentRepository.findById(prescriptionDTO.getAppointmentBookingId())
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + prescriptionDTO.getAppointmentBookingId()));

        Prescription prescription = new Prescription();
        prescription.setDrugName(prescriptionDTO.getDrugName());
        prescription.setAmount(prescriptionDTO.getAmount());
        prescription.setNote(prescriptionDTO.getNote());
        prescription.setAppointment(appointment);

        Prescription savedPrescription = prescriptionRepository.save(prescription);
        return convertToDTO(savedPrescription);
    }

    @Override
    public PrescriptionDTO updatePrescription(PrescriptionDTO prescriptionDTO) {
        // Kiểm tra prescriptionId có null không
        if (prescriptionDTO.getPrescriptionId() == null) {
            throw new IllegalArgumentException("Prescription ID must not be null");
        }

        Prescription prescription = prescriptionRepository.findById(prescriptionDTO.getPrescriptionId())
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

        prescription.setDrugName(prescriptionDTO.getDrugName());
        prescription.setAmount(prescriptionDTO.getAmount());
        prescription.setNote(prescriptionDTO.getNote());

        Prescription updatedPrescription = prescriptionRepository.save(prescription);
        return convertToDTO(updatedPrescription);
    }

    @Override
    public void deletePrescription(Long prescriptionId) {
        prescriptionRepository.deleteById(prescriptionId);
    }

    private PrescriptionDTO convertToDTO(Prescription prescription) {
        return new PrescriptionDTO(
                prescription.getPrescriptionId(),
                prescription.getAppointment().getAppointmentBookingId(),
                prescription.getNote(),
                prescription.getAmount(),
                prescription.getDrugName()
        );
    }
}

