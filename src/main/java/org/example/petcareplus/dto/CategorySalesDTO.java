package org.example.petcareplus.dto;

// DTO
public class CategorySalesDTO {
    private String categoryName;
    private Long totalSold;

    public CategorySalesDTO(String categoryName, Long totalSold) {
        this.categoryName = categoryName;
        this.totalSold = totalSold;
    }
    // getter, setter

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getTotalSold() {
        return totalSold;
    }

    public void setTotalSold(Long totalSold) {
        this.totalSold = totalSold;
    }
}
