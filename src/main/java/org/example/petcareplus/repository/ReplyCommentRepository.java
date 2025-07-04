package org.example.petcareplus.repository;

import org.example.petcareplus.entity.ReplyComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ReplyCommentRepository extends JpaRepository<ReplyComment, Long> {
    List<ReplyComment> findByCommentPost_CommentPostId(Long commentPostId);
}
