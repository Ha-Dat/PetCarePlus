package org.example.petcareplus.dto;

import org.example.petcareplus.entity.Media;
import org.example.petcareplus.entity.Product;
import org.example.petcareplus.enums.ProductStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ProductDTO {
    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;
    private int unitInStock;
    private int unitSold;
    private ProductStatus status;
    private List<Media> medias;
    private Long categoryId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;

    public ProductDTO() {
    }

    public ProductDTO(Long productId, String name, String description, BigDecimal price, int unitInStock, int unitSold, ProductStatus status, List<Media> medias, Long categoryId, Date createdDate) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.unitInStock = unitInStock;
        this.unitSold = unitSold;
        this.status = status;
        this.medias = medias;
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

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public List<Media> getMedias() {
        return medias;
    }

    public void setMedias(List<Media> medias) {
        this.medias = medias;
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