package org.example.petcareplus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "spa_bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpaBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer spaBookingId;

    private LocalDateTime bookDate;
    private String note;
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "pet_profile_id")
    private PetProfile petProfile;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;
}