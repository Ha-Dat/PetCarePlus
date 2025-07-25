package org.example.petcareplus.repository;

import org.example.petcareplus.entity.PostRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRatingRepository extends JpaRepository<PostRating, Long> {
    void deleteByPost_PostId(Long postId);
    Optional<PostRating> findByAccount_AccountIdAndPost_PostId(Long accountId, Long postId);
}
