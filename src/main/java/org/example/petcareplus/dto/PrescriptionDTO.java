package org.example.petcareplus.dto;

import lombok.Data;

@Data
public class PrescriptionDTO {
    private Long appointmentId;
    private String prescriptionNote;

    public PrescriptionDTO() {
    }

    public PrescriptionDTO(Long appointmentId, String prescriptionNote) {
        this.appointmentId = appointmentId;
        this.prescriptionNote = prescriptionNote;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getPrescriptionNote() {
        return prescriptionNote;
    }

    public void setPrescriptionNote(String prescriptionNote) {
        this.prescriptionNote = prescriptionNote;
    }
}
