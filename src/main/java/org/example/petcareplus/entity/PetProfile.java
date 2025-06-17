package org.example.petcareplus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "Pet_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer petProfileId;

    private String name;
    private String species;
    private String breeds;
    private Float weight;
    private Integer age;
    private String image;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @OneToMany(mappedBy = "petProfile")
    private List<AppointmentBooking> appointmentBookings;

    @OneToMany(mappedBy = "petProfile")
    private List<HotelBooking> hotelBookings;

    @OneToMany(mappedBy = "petProfile")
    private List<SpaBooking> spaBookings;
}
