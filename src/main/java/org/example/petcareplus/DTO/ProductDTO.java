package org.example.petcareplus.DTO;

import java.math.BigDecimal;

public class ProductDTO {
    private Integer productId;
    private String name;
    private String description;
    private BigDecimal price;
    private int unitInStock;
    private int unitSold;
    private String status;
    private String image;
    private Integer categoryId;

    // Constructors
    public ProductDTO() {}

    public ProductDTO(Integer productId, String name, String description, BigDecimal price,
                      int unitInStock, int unitSold, String status, String image, Integer categoryId) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.unitInStock = unitInStock;
        this.unitSold = unitSold;
        this.status = status;
        this.image = image;
        this.categoryId = categoryId;
    }

    // Getters and Setters
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getUnitInStock() {
        return unitInStock;
    }

    public void setUnitInStock(int unitInStock) {
        this.unitInStock = unitInStock;
    }

    public int getUnitSold() {
        return unitSold;
    }

    public void setUnitSold(int unitSold) {
        this.unitSold = unitSold;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
}