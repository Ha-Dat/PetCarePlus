package org.example.petcareplus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Wards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer wardId;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id")
    private District district;

    //method thêm trong trường hợp lombok không hoạt động

    public Integer getWardId() {
        return wardId;
    }

    public void setWardId(Integer wardId) {
        this.wardId = wardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }
}
