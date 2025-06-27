package org.example.petcareplus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "Cities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cityId;

    @Column(length = 50)
    private String name;

    @OneToMany(mappedBy = "city")
    private List<District> districts;

    @OneToMany(mappedBy = "city")
    private List<Profile> profiles;

    //method thêm trong trường hợp lombok không hoạt động

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public List<District> getDistricts() {
        return districts;
    }

    public void setDistricts(List<District> districts) {
        this.districts = districts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }
}
