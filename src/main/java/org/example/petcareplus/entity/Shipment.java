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

    public Long getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(Long shipmentId) {
        this.shipmentId = shipmentId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getShipmentCode() {
        return shipmentCode;
    }

    public void setShipmentCode(String shipmentCode) {
        this.shipmentCode = shipmentCode;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public LocalDateTime getExpectedDeliveryTime() {
        return expectedDeliveryTime;
    }

    public void setExpectedDeliveryTime(LocalDateTime expectedDeliveryTime) {
        this.expectedDeliveryTime = expectedDeliveryTime;
    }

    public ShippingStatus getStatus() {
        return status;
    }

    public void setStatus(ShippingStatus status) {
        this.status = status;
    }

    public String getTrackingUrl() {
        return trackingUrl;
    }

    public void setTrackingUrl(String trackingUrl) {
        this.trackingUrl = trackingUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
