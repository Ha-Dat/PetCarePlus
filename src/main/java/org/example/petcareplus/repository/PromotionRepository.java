package org.example.petcareplus.repository;

import org.example.petcareplus.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    Promotion findByTitle(String title);

    @Query("SELECT p FROM Promotion p LEFT JOIN FETCH p.medias WHERE p.promotionId = :id")
    Promotion getPromotionWithMedias(@Param("id") Long id);
}
