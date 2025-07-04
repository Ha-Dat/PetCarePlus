package org.example.petcareplus.repository;

import org.example.petcareplus.entity.CommentPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CommentPostRepository extends JpaRepository<CommentPost, Long> {
    List<CommentPost> findCommentByPost_PostId(Long postId);
}
