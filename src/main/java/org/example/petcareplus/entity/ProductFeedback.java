package org.example.petcareplus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "ProductFeedbacks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;

    private int rating;
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    //method thêm trong trường hợp lombok không hoạt động
    
    // Getters
    public Long getFeedbackId() {
        return feedbackId;
    }
    
    public int getRating() {
        return rating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public Account getAccount() {
        return account;
    }
    
    // Setters
    public void setFeedbackId(Long feedbackId) {
        this.feedbackId = feedbackId;
    }
    
    public void setRating(int rating) {
        this.rating = rating;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public void setAccount(Account account) {
        this.account = account;
    }
    
    // toString method
    @Override
    public String toString() {
        return "ProductFeedback{" +
                "feedbackId=" + feedbackId +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", createdAt=" + createdAt +
                ", product=" + (product != null ? product.getProductId() : null) +
                ", account=" + (account != null ? account.getAccountId() : null) +
                '}';
    }
    
    // equals method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductFeedback that = (ProductFeedback) o;
        return Objects.equals(feedbackId, that.feedbackId);
    }
    
    // hashCode method
    @Override
    public int hashCode() {
        return Objects.hash(feedbackId);
    }
}
