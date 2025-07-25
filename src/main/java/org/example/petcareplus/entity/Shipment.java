package org.example.petcareplus.entity;

import jakarta.persistence.*;
import org.example.petcareplus.enums.ShippingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Shipments")
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shipmentId;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private String shipmentCode;

    private BigDecimal shippingFee;

    private String serviceType;

    private LocalDateTime expectedDeliveryTime;

    @Enumerated(EnumType.STRING)
    private ShippingStatus status;

    private String trackingUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
