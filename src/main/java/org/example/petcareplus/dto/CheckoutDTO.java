package org.example.petcareplus.dto;

import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.Order;

import java.time.LocalDateTime;

public class CheckoutDTO {

    private Account account;
    private LocalDateTime orderDate;
    private String status;
    private String receiverName;
    private String receiverPhone;
    private String deliveryAddress;
    private String shippingMethod;
    private String paymentMethod;
    private String couponCode;
    private String note;
    private Order order;

    public CheckoutDTO() {
    }

    public CheckoutDTO(Account account, LocalDateTime orderDate, String status, String receiverName, String receiverPhone, String deliveryAddress, String shippingMethod, String paymentMethod, String couponCode, String note, Order order) {
        this.account = account;
        this.orderDate = orderDate;
        this.status = status;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.deliveryAddress = deliveryAddress;
        this.shippingMethod = shippingMethod;
        this.paymentMethod = paymentMethod;
        this.couponCode = couponCode;
        this.note = note;
        this.order = order;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
