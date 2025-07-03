package org.example.petcareplus.repository;

import org.example.petcareplus.entity.CommentPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CommentPostRepository extends JpaRepository<CommentPost, Integer> {
    List<CommentPost> findByPost_PostId(Long postId);
}
