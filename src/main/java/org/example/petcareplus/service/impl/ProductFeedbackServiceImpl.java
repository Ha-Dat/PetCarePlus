package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.ProductFeedback;
import org.example.petcareplus.repository.ProductFeedbackRepository;
import org.example.petcareplus.service.ProductFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductFeedbackServiceImpl implements ProductFeedbackService {
    
    @Autowired
    private ProductFeedbackRepository productFeedbackRepository;
    
    @Override
    public List<ProductFeedback> getFeedbacksByProductId(Long productId) {
        return productFeedbackRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }
    
    @Override
    public List<ProductFeedback> getFeedbacksByProductIdAndRating(Long productId, int rating) {
        return productFeedbackRepository.findByProductIdAndRatingOrderByCreatedAtDesc(productId, rating);
    }
    
    @Override
    public long getFeedbackCountByProductId(Long productId) {
        return productFeedbackRepository.countByProductId(productId);
    }
    
    @Override
    public Double getAverageRatingByProductId(Long productId) {
        return productFeedbackRepository.getAverageRatingByProductId(productId);
    }
    
    @Override
    public long getFeedbackCountByProductIdAndRating(Long productId, int rating) {
        return productFeedbackRepository.countByProductIdAndRating(productId, rating);
    }
    
    @Override
    public ProductFeedback saveFeedback(ProductFeedback feedback) {
        return productFeedbackRepository.save(feedback);
    }
    
    @Override
    public List<ProductFeedback> findByProductIdAndAccountId(Long productId, Long accountId) {
        return productFeedbackRepository.findByProductIdAndAccountAccountId(productId, accountId);
    }
}
