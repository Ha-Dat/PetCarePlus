package org.example.petcareplus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer promotionId;

    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private String image;

    @OneToMany(mappedBy = "promotion")
    private List<Order> orders;
}
