package org.example.petcareplus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "AppointmentBookings")
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
}
