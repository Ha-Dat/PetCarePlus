package org.example.petcareplus.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

public class ProductDTO {
    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;
    private int unitInStock;
    private int unitSold;
    private String status;
    private String image;
    private Long categoryId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private Date createdDate;

    public ProductDTO() {
    }

    public ProductDTO(Long productId, String name, String description, BigDecimal price, int unitInStock, int unitSold, String status, String image, Long categoryId, Date createdDate) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.unitInStock = unitInStock;
        this.unitSold = unitSold;
        this.status = status;
        this.image = image;
        this.categoryId = categoryId;
        this.createdDate = createdDate;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
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

    public Long getCategoryId() {
        return categoryId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }


    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}