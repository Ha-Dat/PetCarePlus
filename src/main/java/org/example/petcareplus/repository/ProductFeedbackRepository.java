package org.example.petcareplus.repository;

import org.example.petcareplus.entity.ProductFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductFeedbackRepository extends JpaRepository<ProductFeedback, Long> {
    
    @Query("SELECT pf FROM ProductFeedback pf WHERE pf.product.productId = :productId ORDER BY pf.createdAt DESC")
    List<ProductFeedback> findByProductIdOrderByCreatedAtDesc(@Param("productId") Long productId);
    
    @Query("SELECT pf FROM ProductFeedback pf WHERE pf.product.productId = :productId AND pf.rating = :rating ORDER BY pf.createdAt DESC")
    List<ProductFeedback> findByProductIdAndRatingOrderByCreatedAtDesc(@Param("productId") Long productId, @Param("rating") int rating);
    
    @Query("SELECT COUNT(pf) FROM ProductFeedback pf WHERE pf.product.productId = :productId")
    long countByProductId(@Param("productId") Long productId);
    
    @Query("SELECT AVG(pf.rating) FROM ProductFeedback pf WHERE pf.product.productId = :productId")
    Double getAverageRatingByProductId(@Param("productId") Long productId);
    
    @Query("SELECT COUNT(pf) FROM ProductFeedback pf WHERE pf.product.productId = :productId AND pf.rating = :rating")
    long countByProductIdAndRating(@Param("productId") Long productId, @Param("rating") int rating);
    
    @Query("SELECT pf FROM ProductFeedback pf WHERE pf.product.productId = :productId AND pf.account.accountId = :accountId")
    List<ProductFeedback> findByProductIdAndAccountAccountId(@Param("productId") Long productId, @Param("accountId") Long accountId);
}
