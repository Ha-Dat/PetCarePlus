package org.example.petcareplus.repository;

import org.example.petcareplus.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    Promotion findByTitle(String title);

}
