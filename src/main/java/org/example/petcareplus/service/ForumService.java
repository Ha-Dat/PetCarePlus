package org.example.petcareplus.service;

import org.example.petcareplus.dto.PostDTO;
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
}
