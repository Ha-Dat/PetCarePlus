package org.example.petcareplus.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private String paymentMethod;

    private String transactionCode;
    private BigDecimal amount;
    private String status;
    private LocalDateTime paymentDate;
    private String bankCode;
    private String cardType;
    private String vnpPayDate;
    private String vnpResponseCode;
    private String vnpTransactionType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

