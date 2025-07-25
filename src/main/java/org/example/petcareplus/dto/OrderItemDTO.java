package org.example.petcareplus.dto;

import java.math.BigDecimal;

public class OrderItemDTO {
    private String productName;
    private BigDecimal price;
    private int quantity;

    public OrderItemDTO() {}

    public OrderItemDTO(String productName, BigDecimal price, int quantity) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    public BigDecimal getTotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    // --- Getter & Setter ---
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
