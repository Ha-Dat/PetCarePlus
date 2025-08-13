package org.example.petcareplus.service.impl;

import org.example.petcareplus.dto.PostDTO;
import org.example.petcareplus.entity.Media;
import org.example.petcareplus.entity.Post;
import org.example.petcareplus.repository.MediaRepository;
import org.example.petcareplus.repository.PostRepository;
import org.example.petcareplus.service.PostDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostDashboardServiceImpl implements PostDashboardService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private S3Client s3Client;

    @Override
    public List<PostDTO> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(PostDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public Page<PostDTO> getPostsPaginated(Pageable pageable) {
        Page<Post> postPage = postRepository.findAll(pageable);
        return postPage.map(PostDTO::new);
    }

    @Override
    public PostDTO getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        return new PostDTO(post);
    }

    @Override
    @Transactional
    public void approvePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        post.setChecked(true);
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void rejectPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        
        // Xóa tất cả media files từ S3 trước khi xóa post
        List<Media> mediaList = mediaRepository.findByPost_postId(postId);
        for (Media media : mediaList) {
            if (media.getUrl() != null && !media.getUrl().isBlank()) {
                deleteFileFromS3(media.getUrl());
            }
        }
        
        // Xóa bài viết khi bị từ chối
        postRepository.delete(post);
    }

    @Override
    public List<PostDTO> getPendingPosts() {
        List<Post> pendingPosts = postRepository.findByIsCheckedFalse();
        return pendingPosts.stream()
                .map(PostDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostDTO> getApprovedPosts() {
        List<Post> approvedPosts = postRepository.findByIsCheckedTrue();
        return approvedPosts.stream()
                .map(PostDTO::new)
                .collect(Collectors.toList());
    }

    private void deleteFileFromS3(String key) {
        try {
            // Extract the key from the full URL
            String bucketName = "petcareplus";
            String objectKey = key;
            
            // If the key is a full URL, extract just the object key
            if (key.startsWith("http")) {
                String[] parts = key.split("/");
                objectKey = parts[parts.length - 1];
            }
            
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();
            
            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            // Log the error but don't throw it to avoid breaking the transaction
            System.err.println("Error deleting file from S3: " + e.getMessage());
        }
    }
} 