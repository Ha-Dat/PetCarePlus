package org.example.petcareplus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    private LocalDateTime orderDate;

    private String status;

    private BigDecimal totalPrice;

    @Column(columnDefinition = "TEXT")
    private String deliverAddress;

    private BigDecimal shippingFee;

    private BigDecimal discountAmount;

    @Column(nullable = false)
    private String paymentMethod;

    @Column(columnDefinition = "TEXT")
    private String note;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems;

}
