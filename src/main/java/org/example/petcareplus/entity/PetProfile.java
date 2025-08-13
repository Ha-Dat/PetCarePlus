package org.example.petcareplus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Entity
@Table(name = "pet_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long petProfileId;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "Tên không được để trống")
    @Size(min = 2, max = 50, message = "Tên không được vượt quá 50 ký tự")
    private String name;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "Giống không được để trống")
    @Size(min = 2, max = 50, message = "Giống loài không được vượt quá 50 ký tự")
    private String species;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "Loài không được để trống")
    @Size(min = 2, max = 50, message = "Giống loài không được vượt quá 50 ký tự")
    private String breeds;

    @DecimalMin(value = "0.1", message = "Cân nặng phải lớn hơn 0")
    @DecimalMax(value = "100.0", message = "Cân nặng không vượt quá 200kg")
    private Float weight;

    @Min(1)
    @Max(300)
    private Integer age;

    @OneToMany(mappedBy = "petProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Media> medias;

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

    public @Min(1) @Max(300) Integer getAge() {
        return age;
    }

    public void setAge(@Min(1) @Max(300) Integer age) {
        this.age = age;
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

    public List<Media> getMedias() {
        return medias;
    }

    public void setMedias(List<Media> medias) {
        this.medias = medias;
    }
}
