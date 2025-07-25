package org.example.petcareplus.repository;

import org.example.petcareplus.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaRepository extends JpaRepository<Media, Long> {
    List<Media> findByPost_postId(Long postId);
    Media findByPetProfile_petProfileId(Long petProfileId);
    Media findByProduct_ProductId(Long productId);
    Media findByPromotion_PromotionId(Long promotionId);
}
