package org.example.petcareplus.controller;

import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.Post;
import org.example.petcareplus.enums.AccountRole;
import org.example.petcareplus.enums.AccountStatus;
import org.example.petcareplus.service.ForumService;
import org.example.petcareplus.service.PostDashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PostDashboardControllerApprovePostTest {

    @Mock
    private PostDashboardService postDashboardService;

    @Mock
    private ForumService forumService;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private PostDashboardController postDashboardController;

    private MockHttpSession session;
    private Account testAccount;
    private Post testPost;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        testAccount = new Account();
        testAccount.setAccountId(1L);
        testAccount.setName("testuser");
        testAccount.setPhone("0123456789");
        testAccount.setRole(AccountRole.MANAGER);
        testAccount.setPassword("password123");
        testAccount.setStatus(AccountStatus.ACTIVE);
        
        testPost = new Post();
        testPost.setPostId(1L);
        testPost.setTitle("Test Post");
        testPost.setDescription("Test Description");
        testPost.setChecked(false);
        
        // Set up session with logged in user
        session.setAttribute("loggedInUser", testAccount);
    }

    // ========== APPROVEPOST() METHOD TEST CASES ==========

    @Test
    @DisplayName("UTCID01: Approve Post with NULL PostId")
    void testApprovePost_NullPostId() {
        // Arrange
        Long postId = null;
        
        // Mock service to throw exception for null postId
        doThrow(new IllegalArgumentException("PostID không được để trống"))
            .when(postDashboardService).approvePost(postId);

        // Act
        String result = postDashboardController.approvePost(postId, redirectAttributes);

        // Assert
        assertEquals("redirect:/manager/post-dashboard", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Có lỗi xảy ra khi duyệt bài viết: PostID không được để trống");
    }

    @Test
    @DisplayName("UTCID02: Approve Post with Empty/Invalid PostId")
    void testApprovePost_EmptyInvalidPostId() {
        // Arrange
        Long postId = 0L; // Empty/Invalid format
        
        // Mock service to throw exception for empty/invalid postId
        doThrow(new IllegalArgumentException("PostID không được để trống"))
            .when(postDashboardService).approvePost(postId);

        // Act
        String result = postDashboardController.approvePost(postId, redirectAttributes);

        // Assert
        assertEquals("redirect:/manager/post-dashboard", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Có lỗi xảy ra khi duyệt bài viết: PostID không được để trống");
    }

    @Test
    @DisplayName("UTCID03: Approve Post Successfully")
    void testApprovePost_Success() {
        // Arrange
        Long postId = 1L;

        // Act
        String result = postDashboardController.approvePost(postId, redirectAttributes);

        // Assert
        assertEquals("redirect:/manager/post-dashboard", result);
        verify(postDashboardService).approvePost(postId);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Bài viết đã được duyệt thành công!");
    }

    @Test
    @DisplayName("UTCID04: Approve Post with Non-existent PostId")
    void testApprovePost_NonExistentPostId() {
        // Arrange
        Long postId = -1L;
        
        // Mock service to throw exception for non-existent postId
        doThrow(new RuntimeException("PostID không tồn tại"))
            .when(postDashboardService).approvePost(postId);

        // Act
        String result = postDashboardController.approvePost(postId, redirectAttributes);

        // Assert
        assertEquals("redirect:/manager/post-dashboard", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Có lỗi xảy ra khi duyệt bài viết: PostID không tồn tại");
    }

    @Test
    @DisplayName("UTCID05: Approve Post with Large PostId")
    void testApprovePost_LargePostId() {
        // Arrange
        Long postId = 1000L;

        // Act
        String result = postDashboardController.approvePost(postId, redirectAttributes);

        // Assert
        assertEquals("redirect:/manager/post-dashboard", result);
        verify(postDashboardService).approvePost(postId);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Bài viết đã được duyệt thành công!");
    }

    @Test
    @DisplayName("UTCID06: Approve Post with Server Error")
    void testApprovePost_ServerError() {
        // Arrange
        Long postId = 1000L;
        
        // Mock service to throw server error
        doThrow(new RuntimeException("Server error"))
            .when(postDashboardService).approvePost(postId);

        // Act
        String result = postDashboardController.approvePost(postId, redirectAttributes);

        // Assert
        assertEquals("redirect:/manager/post-dashboard", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Có lỗi xảy ra khi duyệt bài viết: Server error");
    }

    @Test
    @DisplayName("UTCID07: Approve Post with Forbidden Access")
    void testApprovePost_ForbiddenAccess() {
        // Arrange
        Long postId = 1000L;
        
        // Mock service to throw forbidden exception
        doThrow(new SecurityException("403 Forbidden"))
            .when(postDashboardService).approvePost(postId);

        // Act
        String result = postDashboardController.approvePost(postId, redirectAttributes);

        // Assert
        assertEquals("redirect:/manager/post-dashboard", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Có lỗi xảy ra khi duyệt bài viết: 403 Forbidden");
    }

    @Test
    @DisplayName("UTCID08: Approve Post with Unauthenticated User")
    void testApprovePost_UnauthenticatedUser() {
        // Arrange
        Long postId = 1000L;
        
        // Mock service to throw exception for unauthenticated user
        doThrow(new SecurityException("Bạn cần đăng nhập để đánh giá bài viết"))
            .when(postDashboardService).approvePost(postId);

        // Act
        String result = postDashboardController.approvePost(postId, redirectAttributes);

        // Assert
        assertEquals("redirect:/manager/post-dashboard", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Có lỗi xảy ra khi duyệt bài viết: Bạn cần đăng nhập để đánh giá bài viết");
    }
}
