package org.example.petcareplus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrescriptionDTO {
    private Long prescriptionId;

    @NotNull(message = "Appointment ID cannot be null")
    @JsonProperty("appointmentId") // Đảm bảo mapping đúng với JSON
    private Long appointmentBookingId;

    @NotBlank(message = "Drug name cannot be blank")
    private String drugName;

    @NotBlank(message = "Amount cannot be blank")
    private String amount;

    @NotBlank(message = "Note cannot be blank")
    private String note;

    public PrescriptionDTO() {
    }

    public PrescriptionDTO(Long prescriptionId, Long appointmentBookingId, String note, String amount, String drugName) {
        this.appointmentBookingId = appointmentBookingId;
        this.prescriptionId = prescriptionId;
        this.drugName = drugName;
        this.amount = amount;
        this.note = note;
    }

    public Long getAppointmentBookingId() {
        return appointmentBookingId;
    }

    public void setAppointmentBookingId(Long appointmentBookingId) {
        this.appointmentBookingId = appointmentBookingId;
    }

    public Long getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(Long prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
