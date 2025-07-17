package org.example.petcareplus.service.impl;

import org.example.petcareplus.dto.PostDTO;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.CommentPost;
import org.example.petcareplus.entity.Post;
import org.example.petcareplus.entity.ReplyComment;
import org.example.petcareplus.repository.AccountRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ForumServiceImpl implements ForumService {
    private PostRepository postRepository;
    private CommentPostRepository commentPostRepository;
    private ReplyCommentRepository replyCommentRepository;
    private AccountRepository accountRepository;

    @Autowired
    public ForumServiceImpl(PostRepository postRepository, CommentPostRepository commentPostRepository, ReplyCommentRepository replyCommentRepository, AccountRepository accountRepository) {
        this.postRepository = postRepository;
        this.commentPostRepository = commentPostRepository;
        this.replyCommentRepository = replyCommentRepository;
        this.accountRepository = accountRepository;
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

    @Override
    public Post savePost(PostDTO postDTO, Long accountId) {
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setDescription(postDTO.getDescription());
        post.setRating(postDTO.getRating());
        post.setChecked(false);

        // Gán account
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        post.setAccount(account);

        // Xử lý image
        if (postDTO.getImageFile() != null && !postDTO.getImageFile().isEmpty()) {
            String imageName = saveFile(postDTO.getImageFile(), "uploads/images/");
            post.setImage(imageName);
        }

        // Xử lý video
        if (postDTO.getVideoFile() != null && !postDTO.getVideo().isEmpty()) {
            String videoName = saveFile(postDTO.getVideoFile(), "uploads/videos/");
            post.setVideo(videoName);
        }

        return postRepository.save(post);
    }

    public void updatePost(PostDTO postDTO) {
        Post existingPost = postRepository.findById(postDTO.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        existingPost.setTitle(postDTO.getTitle());
        existingPost.setDescription(postDTO.getDescription());
        existingPost.setRating(postDTO.getRating());

        // Nếu có file mới, xử lý upload → set path mới
        if (postDTO.getImageFile() != null && !postDTO.getImageFile().isEmpty()) {
            String imageName = saveFile(postDTO.getImageFile(), "uploads/images/");
            existingPost.setImage(imageName);
        }

        if (postDTO.getVideoFile() != null && !postDTO.getVideoFile().isEmpty()) {
            String videoName = saveFile(postDTO.getVideoFile(), "uploads/videos/");
            existingPost.setVideo(videoName);
        }

        postRepository.save(existingPost);
    }

    public void deletePostById(Long postId) {
        postRepository.deleteById(postId);
    }

    @Override
    public void saveCommentPost(Long postId, Long accountId, String comment) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        CommentPost commentPost = new CommentPost();
        commentPost.setComment(comment);
        commentPost.setAccount(account);
        commentPost.setPost(post);

        commentPostRepository.save(commentPost);
    }

    @Override
    public void saveReplyComment(Long commentId, Long accountId, String content) {
        CommentPost commentPost = commentPostRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        ReplyComment reply = new ReplyComment();
        reply.setComment(content);
        reply.setCommentPost(commentPost);
        reply.setAccount(account);
        replyCommentRepository.save(reply);
    }


    @Override
    public void updateCommentPost(Long commentId, String newContent, Account currentUser) {
        CommentPost comment = commentPostRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getAccount().getAccountId().equals(currentUser.getAccountId())) {
            throw new RuntimeException("Bạn không có quyền sửa comment này!");
        }

        comment.setComment(newContent);
        commentPostRepository.save(comment);
    }

    @Override
    public void updateReplyComment(Long replyId, String newContent, Account currentUser) {
        ReplyComment reply = replyCommentRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("Reply not found"));

        if (!reply.getAccount().getAccountId().equals(currentUser.getAccountId())) {
            throw new RuntimeException("Bạn không có quyền sửa reply này!");
        }

        reply.setComment(newContent);
        replyCommentRepository.save(reply);
    }

    @Override
    public void deleteCommentPostById(Long commentId) {
        commentPostRepository.deleteById(commentId);
    }

    @Override
    public void deleteReplyCommentById(Long replyId) {
        replyCommentRepository.deleteById(replyId);
    }

    private String saveFile(MultipartFile file, String uploadDir) {
        String fileName = file.getOriginalFilename();
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }
        try {
            file.transferTo(new File(uploadDir + fileName));
        } catch (IOException e) {
            throw new RuntimeException("Could not save file: " + fileName, e);
        }
        return fileName;
    }
}
