package org.example.petcareplus.controller;

import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.Category;
import org.example.petcareplus.enums.AccountRole;
import org.example.petcareplus.enums.AccountStatus;
import org.example.petcareplus.service.CategoryService;
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
class CategoryControllerDeleteCategoryTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private CategoryController categoryController;

    private MockHttpSession session;
    private Account testAccount;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        testAccount = new Account();
        testAccount.setAccountId(1L);
        testAccount.setName("testuser");
        testAccount.setPhone("0123456789");
        testAccount.setRole(AccountRole.SELLER);
        testAccount.setPassword("password123");
        testAccount.setStatus(AccountStatus.ACTIVE);
        
        testCategory = new Category();
        testCategory.setCategoryId(1L);
        testCategory.setName("Thức ăn");
        
        // Set up session with logged in user
        session.setAttribute("loggedInUser", testAccount);
    }

    // ========== DELETECATEGORY() METHOD TEST CASES ==========

    @Test
    @DisplayName("UTCID01: Delete Category with NULL CategoryId")
    void testDeleteCategory_NullCategoryId() {
        // Arrange
        Long categoryId = null;
        
        // Mock service to throw exception for null categoryId
        doThrow(new IllegalArgumentException("CategoryID không được để trống"))
            .when(categoryService).deleteCategory(categoryId);

        // Act
        String result = categoryController.deleteCategory(categoryId, redirectAttributes);

        // Assert
        assertEquals("redirect:/seller/category-dashboard", result);
        verify(categoryService).deleteCategory(categoryId);
        verify(redirectAttributes).addFlashAttribute("toastMessage", "Danh mục này đã có danh mục con, không thể xóa!");
        verify(redirectAttributes).addFlashAttribute("toastType", "error");
    }

    @Test
    @DisplayName("UTCID02: Delete Category Successfully")
    void testDeleteCategory_Success() {
        // Arrange
        Long categoryId = 1L;
        
        // Mock service to return true (success)
        when(categoryService.deleteCategory(categoryId)).thenReturn(true);

        // Act
        String result = categoryController.deleteCategory(categoryId, redirectAttributes);

        // Assert
        assertEquals("redirect:/seller/category-dashboard", result);
        verify(categoryService).deleteCategory(categoryId);
        verify(redirectAttributes).addFlashAttribute("toastMessage", "Xóa danh mục thành công!");
        verify(redirectAttributes).addFlashAttribute("toastType", "success");
    }

    @Test
    @DisplayName("UTCID03: Delete Category with Non-existent CategoryId")
    void testDeleteCategory_NonExistentCategoryId() {
        // Arrange
        Long categoryId = 999L; // Non-existent category
        
        // Mock service to throw exception for non-existent category
        doThrow(new IllegalArgumentException("CategoryID không tồn tại"))
            .when(categoryService).deleteCategory(categoryId);

        // Act
        String result = categoryController.deleteCategory(categoryId, redirectAttributes);

        // Assert
        assertEquals("redirect:/seller/category-dashboard", result);
        verify(categoryService).deleteCategory(categoryId);
        verify(redirectAttributes).addFlashAttribute("toastMessage", "Danh mục này đã có danh mục con, không thể xóa!");
        verify(redirectAttributes).addFlashAttribute("toastType", "error");
    }

    @Test
    @DisplayName("UTCID04: Delete Category with Products")
    void testDeleteCategory_WithProducts() {
        // Arrange
        Long categoryId = 1L;
        
        // Mock service to return false (category has products)
        when(categoryService.deleteCategory(categoryId)).thenReturn(false);

        // Act
        String result = categoryController.deleteCategory(categoryId, redirectAttributes);

        // Assert
        assertEquals("redirect:/seller/category-dashboard", result);
        verify(categoryService).deleteCategory(categoryId);
        verify(redirectAttributes).addFlashAttribute("toastMessage", "Danh mục này đã có sản phẩm, không thể xóa!");
        verify(redirectAttributes).addFlashAttribute("toastType", "error");
    }

    @Test
    @DisplayName("UTCID05: Delete Category with Unauthenticated User")
    void testDeleteCategory_UnauthenticatedUser() {
        // Arrange
        Long categoryId = 1L;
        
        // Mock service to throw exception for unauthenticated user
        doThrow(new SecurityException("Bạn cần đăng nhập để xóa danh mục"))
            .when(categoryService).deleteCategory(categoryId);

        // Act
        String result = categoryController.deleteCategory(categoryId, redirectAttributes);

        // Assert
        assertEquals("redirect:/seller/category-dashboard", result);
        verify(categoryService).deleteCategory(categoryId);
        verify(redirectAttributes).addFlashAttribute("toastMessage", "Danh mục này đã có danh mục con, không thể xóa!");
        verify(redirectAttributes).addFlashAttribute("toastType", "error");
    }

    @Test
    @DisplayName("UTCID06: Delete Category with Server Error")
    void testDeleteCategory_ServerError() {
        // Arrange
        Long categoryId = 1L;
        
        // Mock service to throw server error
        doThrow(new RuntimeException("Server error"))
            .when(categoryService).deleteCategory(categoryId);

        // Act
        String result = categoryController.deleteCategory(categoryId, redirectAttributes);

        // Assert
        assertEquals("redirect:/seller/category-dashboard", result);
        verify(categoryService).deleteCategory(categoryId);
        verify(redirectAttributes).addFlashAttribute("toastMessage", "Danh mục này đã có danh mục con, không thể xóa!");
        verify(redirectAttributes).addFlashAttribute("toastType", "error");
    }

    @Test
    @DisplayName("UTCID07: Delete Category with Forbidden Access")
    void testDeleteCategory_ForbiddenAccess() {
        // Arrange
        Long categoryId = 1L;
        
        // Mock service to throw forbidden exception
        doThrow(new SecurityException("403 Forbidden"))
            .when(categoryService).deleteCategory(categoryId);

        // Act
        String result = categoryController.deleteCategory(categoryId, redirectAttributes);

        // Assert
        assertEquals("redirect:/seller/category-dashboard", result);
        verify(categoryService).deleteCategory(categoryId);
        verify(redirectAttributes).addFlashAttribute("toastMessage", "Danh mục này đã có danh mục con, không thể xóa!");
        verify(redirectAttributes).addFlashAttribute("toastType", "error");
    }

    @Test
    @DisplayName("UTCID08: Delete Category with Large CategoryId")
    void testDeleteCategory_LargeCategoryId() {
        // Arrange
        Long categoryId = 1000L;
        
        // Mock service to return true (success)
        when(categoryService.deleteCategory(categoryId)).thenReturn(true);

        // Act
        String result = categoryController.deleteCategory(categoryId, redirectAttributes);

        // Assert
        assertEquals("redirect:/seller/category-dashboard", result);
        verify(categoryService).deleteCategory(categoryId);
        verify(redirectAttributes).addFlashAttribute("toastMessage", "Xóa danh mục thành công!");
        verify(redirectAttributes).addFlashAttribute("toastType", "success");
    }

    @Test
    @DisplayName("UTCID09: Delete Category with Negative CategoryId")
    void testDeleteCategory_NegativeCategoryId() {
        // Arrange
        Long categoryId = -1L;
        
        // Mock service to throw exception for negative categoryId
        doThrow(new IllegalArgumentException("CategoryID không tồn tại"))
            .when(categoryService).deleteCategory(categoryId);

        // Act
        String result = categoryController.deleteCategory(categoryId, redirectAttributes);

        // Assert
        assertEquals("redirect:/seller/category-dashboard", result);
        verify(categoryService).deleteCategory(categoryId);
        verify(redirectAttributes).addFlashAttribute("toastMessage", "Danh mục này đã có danh mục con, không thể xóa!");
        verify(redirectAttributes).addFlashAttribute("toastType", "error");
    }
}
