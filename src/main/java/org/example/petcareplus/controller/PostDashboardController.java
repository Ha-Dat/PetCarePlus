package org.example.petcareplus.controller;

import org.example.petcareplus.dto.PostDTO;
import org.example.petcareplus.entity.Post;
import org.example.petcareplus.service.ForumService;
import org.example.petcareplus.service.PostDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/manager/post-dashboard")
public class PostDashboardController {

    @Autowired
    private PostDashboardService postDashboardService;

    @Autowired
    private ForumService forumService;

    @GetMapping
    public String showDashboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PostDTO> postPage = postDashboardService.getPostsPaginated(pageable);

        // Kiểm tra bài viết chờ duyệt
            List<Post> userPendingPosts = forumService.findPendingPosts().stream().toList();
            if (!userPendingPosts.isEmpty()) {
                model.addAttribute("pendingMessage", "Bạn có " + userPendingPosts.size() + " bài viết đang chờ duyệt");
            }
        
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("totalItems", postPage.getTotalElements());
        model.addAttribute("size", size);
        
        return "post-dashboard";
    }

    @GetMapping("/post/{postId}")
    @ResponseBody
    public ResponseEntity<PostDTO> getPostDetail(@PathVariable Long postId) {
        try {
            PostDTO post = postDashboardService.getPostById(postId);
            if (post != null) {
                return ResponseEntity.ok(post);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/approve/{postId}")
    public String approvePost(@PathVariable Long postId, RedirectAttributes redirectAttributes) {
        try {
            postDashboardService.approvePost(postId);
            redirectAttributes.addFlashAttribute("successMessage", "Bài viết đã được duyệt thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi duyệt bài viết: " + e.getMessage());
        }
        return "redirect:/post-dashboard";
    }

    @PostMapping("/reject/{postId}")
    public String rejectPost(@PathVariable Long postId, RedirectAttributes redirectAttributes) {
        try {
            postDashboardService.rejectPost(postId);
            redirectAttributes.addFlashAttribute("successMessage", "Bài viết đã bị từ chối!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi từ chối bài viết: " + e.getMessage());
        }
        return "redirect:/post-dashboard";
    }

    @GetMapping("/pending")
    public String showPendingPosts(Model model) {
        model.addAttribute("posts", postDashboardService.getPendingPosts());
        model.addAttribute("filterType", "pending");
        return "post-dashboard";
    }

    @GetMapping("/approved")
    public String showApprovedPosts(Model model) {
        model.addAttribute("posts", postDashboardService.getApprovedPosts());
        model.addAttribute("filterType", "approved");
        return "post-dashboard";
    }
} 