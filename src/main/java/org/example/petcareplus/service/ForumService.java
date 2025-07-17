package org.example.petcareplus.service;

import org.example.petcareplus.dto.PostDTO;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.CommentPost;
import org.example.petcareplus.entity.Post;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ForumService {
    Page<Post> findAll(int page, int size, String sortBy);
    Optional<Post> findById(Long id);
    List<CommentPost> findCommentByPostId(Long postId);
    Post savePost(PostDTO postDTO, Long accountId);
    void updatePost(PostDTO postDTO);
    void deletePostById(Long postId);
    void saveCommentPost(Long postId, Long accountId, String comment);
    void saveReplyComment(Long commentId, Long accountId, String content);
    void updateCommentPost(Long commentId, String newContent, Account currentUser);
    void updateReplyComment(Long replyId, String content, Account currentUser);
    void deleteCommentPostById(Long commentId);
    void deleteReplyCommentById(Long replyId);
}
