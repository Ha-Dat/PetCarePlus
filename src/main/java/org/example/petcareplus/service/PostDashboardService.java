package org.example.petcareplus.service;

import org.example.petcareplus.dto.PostDTO;
import org.example.petcareplus.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostDashboardService {
    List<PostDTO> getAllPosts();
    
    Page<PostDTO> getPostsPaginated(Pageable pageable);
    
    PostDTO getPostById(Long postId);
    
    void approvePost(Long postId);
    
    void rejectPost(Long postId);
    
    List<PostDTO> getPendingPosts();
    
    List<PostDTO> getApprovedPosts();
} 