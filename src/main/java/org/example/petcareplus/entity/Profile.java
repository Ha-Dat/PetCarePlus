package org.example.petcareplus.entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer profileId;

    @OneToOne
    @JoinColumn(name = "account_id", unique = true)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    @ManyToOne
    @JoinColumn(name = "ward_id")
    private Ward ward;

    @NotBlank(message = "Giới tính không được để trống")
    private String gender;

    @NotNull(message = "Ngày sinh không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    private String avatarPath;

    @OneToMany(mappedBy = "profile")
    private List<PetProfile> petProfiles;

    //method thêm trong trường hợp lombok không hoạt động
    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Integer getDistrictId() {
        return districtId;
    }
x   
    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
    }

    public Integer getWardId() {
        return wardId;
    }

    public void setWardId(Integer wardId) {
        this.wardId = wardId;
    }

    public List<PetProfile> getPetProfiles() {
        return petProfiles;
    }

    public void setPetProfiles(List<PetProfile> petProfiles) {
        this.petProfiles = petProfiles;
    }
}
