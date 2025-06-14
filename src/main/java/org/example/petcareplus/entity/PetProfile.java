package org.example.petcareplus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Entity
@Table(name = "PetProfiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer petProfileId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String species;

    @Column(nullable = false)
    private String breeds;

    private Float weight;

    @Size(min = 1, max = 50)
    private Integer age;

    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @OneToMany(mappedBy = "petProfile")
    private List<AppointmentBooking> appointmentBookings;

    @OneToMany(mappedBy = "petProfile")
    private List<HotelBooking> hotelBookings;

    @OneToMany(mappedBy = "petProfile")
    private List<SpaBooking> spaBookings;
}
