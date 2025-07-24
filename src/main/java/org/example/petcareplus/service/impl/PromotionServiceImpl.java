package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.Promotion;
import org.example.petcareplus.repository.PromotionRepository;
import org.example.petcareplus.service.PromotionService;
import org.springframework.stereotype.Service;

@Service
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    public PromotionServiceImpl(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    @Override
    public Promotion findByTitle(String title) {
        return promotionRepository.findByTitle(title);
    }
}
