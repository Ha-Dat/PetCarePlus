package org.example.petcareplus.service;

import org.example.petcareplus.entity.Promotion;

import java.util.List;

public interface PromotionService {

    Promotion findByTitle(String title);

    List<Promotion> getAllPromotions();

    void savePromotion(Promotion promotion);

    void updatePromotion(Promotion promotion);

    Promotion getPromotionById(Long id);

    Promotion getPromotionWithMedias(Long id);
}
