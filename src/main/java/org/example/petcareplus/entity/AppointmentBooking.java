package org.example.petcareplus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointmentbookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentBookingId;

    @Column(nullable = false)
    private LocalDateTime bookDate;

    private String note;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "pet_profile_id")
    private PetProfile petProfile;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    //method thêm trong trường hợp lombok không hoạt động

    public Long getAppointmentBookingId() {
        return appointmentBookingId;
    }

    public void setAppointmentBookingId(Long appointmentBookingId) {
        this.appointmentBookingId = appointmentBookingId;
    }

    public LocalDateTime getBookDate() {
        return bookDate;
    }

    public void setBookDate(LocalDateTime bookDate) {
        this.bookDate = bookDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public PetProfile getPetProfile() {
        return petProfile;
    }

    public void setPetProfile(PetProfile petProfile) {
        this.petProfile = petProfile;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
