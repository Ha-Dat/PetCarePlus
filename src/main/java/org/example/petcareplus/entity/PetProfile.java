package org.example.petcareplus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Entity
@Table(name = "petprofiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long petProfileId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String species;

    @Column(nullable = false)
    private String breeds;

    private Float weight;

    @Min(1)
    @Max(50)
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

    //method thêm trong trường hợp lombok không hoạt động
    public Long getPetProfileId() {
        return petProfileId;
    }

    public void setPetProfileId(Long petProfileId) {
        this.petProfileId = petProfileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreeds() {
        return breeds;
    }

    public void setBreeds(String breeds) {
        this.breeds = breeds;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public @Min(1) @Max(50) Integer getAge() {
        return age;
    }

    public void setAge(@Min(1) @Max(50) Integer age) {
        this.age = age;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public List<AppointmentBooking> getAppointmentBookings() {
        return appointmentBookings;
    }

    public void setAppointmentBookings(List<AppointmentBooking> appointmentBookings) {
        this.appointmentBookings = appointmentBookings;
    }

    public List<HotelBooking> getHotelBookings() {
        return hotelBookings;
    }

    public void setHotelBookings(List<HotelBooking> hotelBookings) {
        this.hotelBookings = hotelBookings;
    }

    public List<SpaBooking> getSpaBookings() {
        return spaBookings;
    }

    public void setSpaBookings(List<SpaBooking> spaBookings) {
        this.spaBookings = spaBookings;
    }
}
