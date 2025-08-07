package org.example.petcareplus.service.impl;

import org.example.petcareplus.dto.PostDTO;
import org.example.petcareplus.entity.*;
import org.example.petcareplus.enums.MediaCategory;
import org.example.petcareplus.enums.Rating;
import org.example.petcareplus.repository.*;
import org.example.petcareplus.service.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
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
    private MediaRepository mediaRepository;
    private S3Client s3Client;

    @Autowired
    public ForumServiceImpl(PostRepository postRepository, CommentPostRepository commentPostRepository, ReplyCommentRepository replyCommentRepository, AccountRepository accountRepository, PostRatingRepository postRatingRepository, MediaRepository mediaRepository, S3Client s3Client) {
        this.postRepository = postRepository;
        this.commentPostRepository = commentPostRepository;
        this.replyCommentRepository = replyCommentRepository;
        this.accountRepository = accountRepository;
        this.postRatingRepository = postRatingRepository;
        this.mediaRepository = mediaRepository;
        this.s3Client = s3Client;
    }

    @Override
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Override
    public List<Post> findAllWithMedias() {
        return postRepository.findAllWithMedias();
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
        List<Media> medias = new ArrayList<>();
        post.setTitle(postDTO.getTitle());
        post.setDescription(postDTO.getDescription());
        post.setChecked(false);

        // Gán account
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        post.setAccount(account);


        // Lưu ảnh
        if (!postDTO.getImageFiles().isEmpty()) {
            for (MultipartFile imageFile : postDTO.getImageFiles()) {
                if(!imageFile.isEmpty()) {
                    String imageUrl = saveFileToS3(imageFile, "uploads/images/");
                    Media media = new Media();
                    media.setMediaCategory(MediaCategory.POST_IMAGE);
                    media.setUrl(imageUrl);
                    media.setPost(post);
                    medias.add(media);
                }
            }
        }

        // Lưu video
        if (!postDTO.getVideoFiles().isEmpty()) {
            for (MultipartFile videoFile : postDTO.getVideoFiles()) {
                if(!videoFile.isEmpty()) {
                    String videoUrl = saveFileToS3(videoFile, "uploads/videos/");
                    Media media = new Media();
                    media.setMediaCategory(MediaCategory.POST_VIDEO);
                    media.setUrl(videoUrl);
                    media.setPost(post);
                    medias.add(media);
                }
            }
        }

        postRepository.save(post);
        mediaRepository.saveAll(medias);
    }

    @Transactional
    public void updatePost(PostDTO postDTO) {
        Post existingPost = postRepository.findById(postDTO.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        existingPost.setTitle(postDTO.getTitle());
        existingPost.setDescription(postDTO.getDescription());

        // Lấy media cũ trong DB
        List<Media> existingMedias = mediaRepository.findByPost_postId(existingPost.getPostId());

        // Xác định media cần xoá
        List<Media> mediasToKeep = new ArrayList<>();
        List<Media> mediasToDelete = new ArrayList<>();

        for (Media media : existingMedias) {
            String url = media.getUrl();
            boolean keep = false;

            if (media.getMediaCategory() == MediaCategory.POST_IMAGE && postDTO.getOldImageUrls() != null) {
                keep = postDTO.getOldImageUrls().contains(url);
            } else if (media.getMediaCategory() == MediaCategory.POST_VIDEO && postDTO.getOldVideoUrls() != null) {
                keep = postDTO.getOldVideoUrls().contains(url);
            }

            if (keep) {
                mediasToKeep.add(media);
            } else {
                mediasToDelete.add(media);
            }
        }

        // Xoá file & record
        for (Media media : mediasToDelete) {
            deleteFileFromS3(media.getUrl());
        }
        mediaRepository.deleteAll(mediasToDelete);

        List<Media> newMedias = new ArrayList<>(mediasToKeep);

        // Upload ảnh mới
        if (postDTO.getImageFiles() != null) {
            for (MultipartFile imageFile : postDTO.getImageFiles()) {
                if (!imageFile.isEmpty()) {
                    String imageUrl = saveFileToS3(imageFile, "uploads/images/");
                    Media media = new Media();
                    media.setMediaCategory(MediaCategory.POST_IMAGE);
                    media.setUrl(imageUrl);
                    media.setPost(existingPost);
                    newMedias.add(media);
                }
            }
        }

        // Upload video mới
        if (postDTO.getVideoFiles() != null) {
            for (MultipartFile videoFile : postDTO.getVideoFiles()) {
                if (!videoFile.isEmpty()) {
                    String videoUrl = saveFileToS3(videoFile, "uploads/videos/");
                    Media media = new Media();
                    media.setMediaCategory(MediaCategory.POST_VIDEO);
                    media.setUrl(videoUrl);
                    media.setPost(existingPost);
                    newMedias.add(media);
                }
            }
        }

        // Lưu lại media (chỉ cần xoá hết cũ thì saveAll cái mới thôi)
        mediaRepository.saveAll(newMedias);

        // Cập nhật post
        postRepository.save(existingPost);
    }



    public void deletePostById(Long postId) {
        List<Media> media = mediaRepository.findByPost_postId(postId);
        for(Media me : media) {
            if(me.getUrl() != null && !me.getUrl().isBlank()) {
                deleteFileFromS3(me.getUrl());
            }
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

    @Override
    public Page<Post> getPostsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("postId").ascending());
        return postRepository.findAll(pageable);
    }

    @Override
    public void updatePostStatus(Long postId, boolean status) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setChecked(status);
            postRepository.save(post);
        } else {
            throw new RuntimeException("Không tìm thấy bài viết với ID: " + postId);
        }
    }

    @Override
    public List<Post> findApprovedPosts() {
        return postRepository.findApprovedPostsOrderByCreatedAtDesc();
    }

    @Override
    public List<Post> findPendingPosts() {
        return postRepository.findByIsCheckedFalse();
    }

    @Override
    public List<Post> findTop6NewestPosts() {
        Pageable pageable = PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "createdAt"));
        return postRepository.findAll(pageable).getContent();
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

        return "https://petcareplus.s3.ap-southeast-2.amazonaws.com/" + key;
    }

    public void deleteFileFromS3(String url) {
        String prefix = "https://petcareplus.s3.ap-southeast-2.amazonaws.com/";
        String key = url.replace(prefix, "");

        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket("petcareplus")
                        .key(key)
                        .build()
        );
    }
}
