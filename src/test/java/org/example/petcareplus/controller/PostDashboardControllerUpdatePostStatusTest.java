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
class PostDashboardControllerUpdatePostStatusTest {

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

    // ========== UPDATEPOSTSTATUS() METHOD TEST CASES ==========

    @Test
    @DisplayName("UTCID01: Update Post Status with NULL PostId")
    void testUpdatePostStatus_NullPostId() {
        // Arrange
        Long postId = null;
        String status = "Duyệt";
        
        // Mock service to throw exception for null postId
        doThrow(new IllegalArgumentException("PostID không được để trống"))
            .when(forumService).updatePostStatus(postId, true);

        // Act
        String result = postDashboardController.updatePostStatus(postId, status, redirectAttributes);

        // Assert
        assertEquals("redirect:/manager/post-dashboard", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật trạng thái bài viết: PostID không được để trống");
    }

    @Test
    @DisplayName("UTCID02: Update Post Status Successfully - Approve")
    void testUpdatePostStatus_SuccessApprove() {
        // Arrange
        Long postId = 1L;
        String status = "Duyệt";

        // Act
        String result = postDashboardController.updatePostStatus(postId, status, redirectAttributes);

        // Assert
        assertEquals("redirect:/manager/post-dashboard", result);
        verify(forumService).updatePostStatus(postId, true);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Bài viết đã được duyệt thành công!");
    }

    @Test
    @DisplayName("UTCID03: Update Post Status with Non-existent PostId")
    void testUpdatePostStatus_NonExistentPostId() {
        // Arrange
        Long postId = -1L;
        String status = "Duyệt";
        
        // Mock service to throw exception for non-existent postId
        doThrow(new RuntimeException("PostID không tồn tại"))
            .when(forumService).updatePostStatus(postId, true);

        // Act
        String result = postDashboardController.updatePostStatus(postId, status, redirectAttributes);

        // Assert
        assertEquals("redirect:/manager/post-dashboard", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật trạng thái bài viết: PostID không tồn tại");
    }

    @Test
    @DisplayName("UTCID04: Update Post Status Successfully - Reject")
    void testUpdatePostStatus_SuccessReject() {
        // Arrange
        Long postId = -1L;
        String status = "Từ chối";

        // Act
        String result = postDashboardController.updatePostStatus(postId, status, redirectAttributes);

        // Assert
        assertEquals("redirect:/manager/post-dashboard", result);
        verify(postDashboardService).rejectPost(postId);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Bài viết đã bị từ chối!");
    }

    @Test
    @DisplayName("UTCID05: Update Post Status with Unauthenticated User")
    void testUpdatePostStatus_UnauthenticatedUser() {
        // Arrange
        Long postId = 1000L;
        String status = "Duyệt";
        
        // Mock service to throw exception for unauthenticated user
        doThrow(new SecurityException("Bạn cần đăng nhập để đánh giá bài viết"))
            .when(forumService).updatePostStatus(postId, true);

        // Act
        String result = postDashboardController.updatePostStatus(postId, status, redirectAttributes);

        // Assert
        assertEquals("redirect:/manager/post-dashboard", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật trạng thái bài viết: Bạn cần đăng nhập để đánh giá bài viết");
    }

    @Test
    @DisplayName("UTCID06: Update Post Status with Server Error")
    void testUpdatePostStatus_ServerError() {
        // Arrange
        Long postId = 1000L;
        String status = "Từ chối";
        
        // Mock service to throw server error
        doThrow(new RuntimeException("Server error"))
            .when(postDashboardService).rejectPost(postId);

        // Act
        String result = postDashboardController.updatePostStatus(postId, status, redirectAttributes);

        // Assert
        assertEquals("redirect:/manager/post-dashboard", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật trạng thái bài viết: Server error");
    }

    @Test
    @DisplayName("UTCID07: Update Post Status with Forbidden Access")
    void testUpdatePostStatus_ForbiddenAccess() {
        // Arrange
        Long postId = 1000L;
        String status = "Duyệt";
        
        // Mock service to throw forbidden exception
        doThrow(new SecurityException("403 Forbidden"))
            .when(forumService).updatePostStatus(postId, true);

        // Act
        String result = postDashboardController.updatePostStatus(postId, status, redirectAttributes);

        // Assert
        assertEquals("redirect:/manager/post-dashboard", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật trạng thái bài viết: 403 Forbidden");
    }

    @Test
    @DisplayName("UTCID08: Update Post Status with Large PostId")
    void testUpdatePostStatus_LargePostId() {
        // Arrange
        Long postId = 1000L;
        String status = "Từ chối";

        // Act
        String result = postDashboardController.updatePostStatus(postId, status, redirectAttributes);

        // Assert
        assertEquals("redirect:/manager/post-dashboard", result);
        verify(postDashboardService).rejectPost(postId);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Bài viết đã bị từ chối!");
    }

    @Test
    @DisplayName("UTCID09: Update Post Status with Invalid Status")
    void testUpdatePostStatus_InvalidStatus() {
        // Arrange
        Long postId = 1000L;
        String status = "Invalid Status";

        // Act
        String result = postDashboardController.updatePostStatus(postId, status, redirectAttributes);

        // Assert
        assertEquals("redirect:/manager/post-dashboard", result);
        // Should not call any service method for invalid status
        verify(forumService, never()).updatePostStatus(anyLong(), anyBoolean());
        verify(postDashboardService, never()).rejectPost(anyLong());
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Trạng thái không hợp lệ");
    }
}
