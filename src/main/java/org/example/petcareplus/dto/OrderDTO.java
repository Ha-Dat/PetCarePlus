package org.example.petcareplus.dto;

import org.example.petcareplus.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderDTO {
    private Long orderId;
    private String customerName;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;
    private LocalDateTime orderDate;
    private String paymentMethod;
    private String address;


    public OrderDTO() {}

    public OrderDTO(Long orderId, String customerName, OrderStatus status, BigDecimal totalPrice,
                    LocalDateTime orderDate, String paymentMethod, String address) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.status = status;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.paymentMethod = paymentMethod;
        this.address = address;
    }

    public OrderDTO(Long orderId, String customerName, OrderStatus status, BigDecimal totalPrice,
                    BigDecimal shippingFee, BigDecimal discountAmount, LocalDateTime orderDate, 
                    String paymentMethod, String address) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.status = status;
        this.totalPrice = totalPrice;
        this.shippingFee = shippingFee;
        this.discountAmount = discountAmount;
        this.orderDate = orderDate;
        this.paymentMethod = paymentMethod;
        this.address = address;
    }

    public String getFormattedTotalPrice() {
        if (totalPrice == null) return "0 đ";
        return String.format("%,.0f đ", totalPrice.doubleValue()); // format như: 12,000 đ
    }

    public String getFormattedShippingFee() {
        if (shippingFee == null) return "0 đ";
        return String.format("%,.0f đ", shippingFee.doubleValue());
    }

    public String getFormattedDiscountAmount() {
        if (discountAmount == null) return "0 đ";
        return String.format("%,.0f đ", discountAmount.doubleValue());
    }

    public String getFormattedSubtotal() {
        if (totalPrice == null || shippingFee == null || discountAmount == null) return "0 đ";
        BigDecimal subtotal = totalPrice.subtract(shippingFee).add(discountAmount);
        return String.format("%,.0f đ", subtotal.doubleValue());
    }


    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
}
