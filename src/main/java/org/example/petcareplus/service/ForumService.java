package org.example.petcareplus.service;

import org.example.petcareplus.dto.PostDTO;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.CommentPost;
import org.example.petcareplus.enums.Rating;
import org.example.petcareplus.entity.Post;

import java.util.List;
import java.util.Optional;

public interface ForumService {
    List<Post> findAll();

    List<Post> findAllWithMedias();

    Optional<Post> findById(Long id);

    List<CommentPost> findCommentByPostId(Long postId);

    void savePost(PostDTO postDTO, Long accountId);

    void updatePost(PostDTO postDTO);

    void deletePostById(Long postId);

    void saveCommentPost(Long postId, Long accountId, String comment);

    void saveReplyComment(Long commentId, Long accountId, String content);

    void updateCommentPost(Long commentId, String newContent, Account currentUser);

    void updateReplyComment(Long replyId, String content, Account currentUser);

    void deleteCommentPostById(Long commentId);

    void deleteReplyCommentById(Long replyId);

    void saveRating(Long postId, Long accountId, Rating rating);
}
