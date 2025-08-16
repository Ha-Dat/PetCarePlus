package org.example.petcareplus.service;

import org.example.petcareplus.entity.ProductFeedback;
import java.util.List;

public interface ProductFeedbackService {
    
    List<ProductFeedback> getFeedbacksByProductId(Long productId);
    
    List<ProductFeedback> getFeedbacksByProductIdAndRating(Long productId, int rating);
    
    long getFeedbackCountByProductId(Long productId);
    
    Double getAverageRatingByProductId(Long productId);
    
    long getFeedbackCountByProductIdAndRating(Long productId, int rating);
    
    ProductFeedback saveFeedback(ProductFeedback feedback);
    
    List<ProductFeedback> findByProductIdAndAccountId(Long productId, Long accountId);
}
