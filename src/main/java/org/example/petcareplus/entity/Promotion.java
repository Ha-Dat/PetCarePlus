package org.example.petcareplus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer promotionId;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false, scale = 2)
    @Size(min = 1, max = 100)
    private BigDecimal discount;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String status;

    private String image;

    @OneToMany(mappedBy = "promotion")
    private List<Order> orders;

    //method thêm trong trường hợp lombok không hoạt động
}
