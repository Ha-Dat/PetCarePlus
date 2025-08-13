package org.example.petcareplus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.petcareplus.enums.ServiceCategory;

@Entity
@Table(name = "Prescriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long prescriptionId;

    private String prescriptionNote;

    @OneToOne
    @JoinColumn(name = "appointment_id", unique = true, nullable = false)
    private AppointmentBooking appointment;

    public void setAppointment(AppointmentBooking appointment) {
        // Business rule: only allow linking to appointments with service category APPOINTMENT
        if (appointment != null &&
                appointment.getService() != null &&
                appointment.getService().getServiceCategory() == ServiceCategory.APPOINTMENT) {
            this.appointment = appointment;
        } else {
            throw new IllegalArgumentException("Prescription can only be linked to appointments with service category APPOINTMENT.");
        }
    }

    public Long getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(Long prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public String getPrescriptionNote() {
        return prescriptionNote;
    }

    public void setPrescriptionNote(String prescriptionNote) {
        this.prescriptionNote = prescriptionNote;
    }

    public AppointmentBooking getAppointment() {
        return appointment;
    }
}
