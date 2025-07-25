package org.example.petcareplus.service.impl;

import org.example.petcareplus.dto.PostDTO;
import org.example.petcareplus.entity.*;
import org.example.petcareplus.enums.Rating;
import org.example.petcareplus.repository.*;
import org.example.petcareplus.service.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ForumServiceImpl implements ForumService {
    private PostRepository postRepository;
    private CommentPostRepository commentPostRepository;
    private ReplyCommentRepository replyCommentRepository;
    private AccountRepository accountRepository;
    private PostRatingRepository postRatingRepository;
    private S3Client s3Client;

    @Autowired
    public ForumServiceImpl(PostRepository postRepository, CommentPostRepository commentPostRepository, ReplyCommentRepository replyCommentRepository, AccountRepository accountRepository, PostRatingRepository postRatingRepository, S3Client s3Client) {
        this.postRepository = postRepository;
        this.commentPostRepository = commentPostRepository;
        this.replyCommentRepository = replyCommentRepository;
        this.accountRepository = accountRepository;
        this.postRatingRepository = postRatingRepository;
        this.s3Client = s3Client;
    }

    @Override
    public List<Post> findAll() {
        return postRepository.findAll();
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
    public void savePost(PostDTO postDTO, Long accountId) {
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setDescription(postDTO.getDescription());
        post.setChecked(false);

        // Gán account
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        post.setAccount(account);


        if (!postDTO.getImageFile().isEmpty()) {
            String image = saveFileToS3(postDTO.getImageFile(), "uploads/images/");
            post.setImage(image);
        }

        if (!postDTO.getVideoFile().isEmpty()) {
            String video = saveFileToS3(postDTO.getVideoFile(), "uploads/videos/");
            post.setVideo(video);
        }

        postRepository.save(post);
    }

    @Transactional
    public void updatePost(PostDTO postDTO) {
        Post existingPost = postRepository.findById(postDTO.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        existingPost.setTitle(postDTO.getTitle());
        existingPost.setDescription(postDTO.getDescription());
        postRatingRepository.deleteByPost_PostId(postDTO.getPostId());

        if (!postDTO.getImageFile().isEmpty()) {
            if (existingPost.getImage() != null){deleteFileFromS3(existingPost.getImage());}
            String image = saveFileToS3(postDTO.getImageFile(), "uploads/images/");
            existingPost.setImage(image);
        }

        if (!postDTO.getVideoFile().isEmpty()) {
            if (existingPost.getVideo() != null){deleteFileFromS3(existingPost.getVideo());}
            String video = saveFileToS3(postDTO.getVideoFile(), "uploads/videos/");
            existingPost.setVideo(video);
        }

        postRepository.save(existingPost);
    }

    public void deletePostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if(post.getImage() != null){
            deleteFileFromS3(post.getImage());
        }
        if(post.getVideo() != null){
            deleteFileFromS3(post.getVideo());
        }
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

    @Override
    public void saveRating(Long postId, Long accountId, Rating rating) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        PostRating checkPostRating = postRatingRepository.findByAccount_AccountIdAndPost_PostId(accountId, postId).orElse(null);
        if (checkPostRating != null) {
            postRatingRepository.delete(checkPostRating);
            if (checkPostRating.getRating().equals(rating)){
                postRatingRepository.delete(checkPostRating);
            }
        } else {
        PostRating postRating = new PostRating();
        postRating.setPost(post);
        postRating.setAccount(account);
        postRating.setRating(rating);
        postRatingRepository.save(postRating);
        }
    }

    //lưu file lên S3
    private String saveFileToS3(MultipartFile file, String folder) {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String key = folder + fileName;

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket("petcareplus")
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    software.amazon.awssdk.core.sync.RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload to S3", e);
        }

        return key; // Hoặc URL đầy đủ nếu bạn muốn lưu link luôn
    }

    public void deleteFileFromS3(String key) {
        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket("petcareplus")
                        .key(key)
                        .build()
        );
    }
}
