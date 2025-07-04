package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.CommentPost;
import org.example.petcareplus.entity.Post;
import org.example.petcareplus.repository.CommentPostRepository;
import org.example.petcareplus.repository.PostRepository;
import org.example.petcareplus.repository.ReplyCommentRepository;
import org.example.petcareplus.service.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ForumServiceImpl implements ForumService {
    private PostRepository postRepository;
    private CommentPostRepository commentPostRepository;
    private ReplyCommentRepository replyCommentRepository;

    @Autowired
    public ForumServiceImpl(PostRepository postRepository, CommentPostRepository commentPostRepository, ReplyCommentRepository replyCommentRepository) {
        this.postRepository = postRepository;
        this.commentPostRepository = commentPostRepository;
        this.replyCommentRepository = replyCommentRepository;
    }

    @Override
    public Page<Post> findAll(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("rating").descending());
        Page<Post> postsPage = postRepository.findAll(pageable);
        return postsPage;
    }

    @Override
    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    @Override
    public List<CommentPost> findCommentByPostId(Long postId) {
        return commentPostRepository.findCommentByPost_PostId(postId);
    }
}
