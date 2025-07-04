package org.example.petcareplus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Orders")
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    private Integer orderId;

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

    //method thêm trong trường hợp lombok không hoạt động

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getDeliverAddress() {
        return deliverAddress;
    }

    public void setDeliverAddress(String deliverAddress) {
        this.deliverAddress = deliverAddress;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
