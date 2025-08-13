package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.Promotion;
import org.example.petcareplus.repository.PromotionRepository;
import org.example.petcareplus.service.PromotionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    @Override
    public void savePromotion(Promotion promotion) {
        promotionRepository.save(promotion);
    }

    @Override
    public void updatePromotion(Promotion promotion) {
        promotionRepository.save(promotion);
    }

    @Override
    public Promotion getPromotionById(Long id) {
        return promotionRepository.findById(id).orElse(null);
    }

    @Override
    public Promotion getPromotionWithMedias(Long id) {
        return promotionRepository.getPromotionWithMedias(id);
    }

    @Override
    public Page<Promotion> getAllPromotions(Pageable pageable) {
        return promotionRepository.findAll(pageable);
    }

}
