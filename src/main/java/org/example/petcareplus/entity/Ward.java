package org.example.petcareplus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "wards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer wardId;

    private String name;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;
}
