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
class CategoryControllerUpdateCategoryTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private CategoryController categoryController;

    private MockHttpSession session;
    private Account testAccount;
    private Category testCategory;
    private Category testParentCategory;

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
        
        testParentCategory = new Category();
        testParentCategory.setCategoryId(2L);
        testParentCategory.setName("Thức ăn khô");
        
        // Set up session with logged in user
        session.setAttribute("loggedInUser", testAccount);
    }

    // ========== UPDATECATEGORY() METHOD TEST CASES ==========

    @Test
    @DisplayName("UTCID01: Update Category with NULL Name")
    void testUpdateCategory_NullName() {
        // Arrange
        Long categoryId = 1L;
        String name = null;
        Long parentId = 2L;
        
        // Mock service to throw exception for null name
        doThrow(new IllegalArgumentException("Tên danh mục không được bỏ trống"))
            .when(categoryService).updateCategory(categoryId, name, parentId);

        // Act
        String result = categoryController.updateCategory(categoryId, name, parentId, redirectAttributes);

        // Assert
        assertEquals("redirect:/seller/category-dashboard", result);
        verify(categoryService).updateCategory(categoryId, name, parentId);
        verify(redirectAttributes).addFlashAttribute("toastMessage", "Cập nhật thất bại!");
        verify(redirectAttributes).addFlashAttribute("toastType", "error");
    }

    @Test
    @DisplayName("UTCID02: Update Category Successfully")
    void testUpdateCategory_Success() {
        // Arrange
        Long categoryId = 1L;
        String name = "Thức ăn";
        Long parentId = 2L;
        
        // Mock service to return true (success)
        when(categoryService.updateCategory(categoryId, name, parentId)).thenReturn(true);

        // Act
        String result = categoryController.updateCategory(categoryId, name, parentId, redirectAttributes);

        // Assert
        assertEquals("redirect:/seller/category-dashboard", result);
        verify(categoryService).updateCategory(categoryId, name, parentId);
        verify(redirectAttributes).addFlashAttribute("toastMessage", "Cập nhật thành công!");
        verify(redirectAttributes).addFlashAttribute("toastType", "success");
    }

    @Test
    @DisplayName("UTCID03: Update Category with Empty Name")
    void testUpdateCategory_EmptyName() {
        // Arrange
        Long categoryId = 1L;
        String name = "";
        Long parentId = 2L;
        
        // Mock service to throw exception for empty name
        doThrow(new IllegalArgumentException("Tên danh mục không được bỏ trống"))
            .when(categoryService).updateCategory(categoryId, name, parentId);

        // Act
        String result = categoryController.updateCategory(categoryId, name, parentId, redirectAttributes);

        // Assert
        assertEquals("redirect:/seller/category-dashboard", result);
        verify(categoryService).updateCategory(categoryId, name, parentId);
        verify(redirectAttributes).addFlashAttribute("toastMessage", "Cập nhật thất bại!");
        verify(redirectAttributes).addFlashAttribute("toastType", "error");
    }

    @Test
    @DisplayName("UTCID04: Update Category with Valid Parent Category")
    void testUpdateCategory_ValidParentCategory() {
        // Arrange
        Long categoryId = 1L;
        String name = "Thức ăn";
        Long parentId = 2L;
        
        // Mock service to return true (success)
        when(categoryService.updateCategory(categoryId, name, parentId)).thenReturn(true);

        // Act
        String result = categoryController.updateCategory(categoryId, name, parentId, redirectAttributes);

        // Assert
        assertEquals("redirect:/seller/category-dashboard", result);
        verify(categoryService).updateCategory(categoryId, name, parentId);
        verify(redirectAttributes).addFlashAttribute("toastMessage", "Cập nhật thành công!");
        verify(redirectAttributes).addFlashAttribute("toastType", "success");
    }

    @Test
    @DisplayName("UTCID05: Update Category with Non-existent Parent Category")
    void testUpdateCategory_NonExistentParentCategory() {
        // Arrange
        Long categoryId = 1L;
        String name = "Thức ăn";
        Long parentId = 999L; // Non-existent parent
        
        // Mock service to throw exception for non-existent parent
        doThrow(new IllegalArgumentException("danh mục cha không tồn tại"))
            .when(categoryService).updateCategory(categoryId, name, parentId);

        // Act
        String result = categoryController.updateCategory(categoryId, name, parentId, redirectAttributes);

        // Assert
        assertEquals("redirect:/seller/category-dashboard", result);
        verify(categoryService).updateCategory(categoryId, name, parentId);
        verify(redirectAttributes).addFlashAttribute("toastMessage", "Cập nhật thất bại!");
        verify(redirectAttributes).addFlashAttribute("toastType", "error");
    }

    @Test
    @DisplayName("UTCID06: Update Category with Non-existent Parent Category in Database")
    void testUpdateCategory_NonExistentParentCategoryInDatabase() {
        // Arrange
        Long categoryId = 1L;
        String name = "Thức ăn";
        Long parentId = 999L; // Non-existent parent in database
        
        // Mock service to throw exception for non-existent parent in database
        doThrow(new IllegalArgumentException("danh mục cha không tồn tại"))
            .when(categoryService).updateCategory(categoryId, name, parentId);

        // Act
        String result = categoryController.updateCategory(categoryId, name, parentId, redirectAttributes);

        // Assert
        assertEquals("redirect:/seller/category-dashboard", result);
        verify(categoryService).updateCategory(categoryId, name, parentId);
        verify(redirectAttributes).addFlashAttribute("toastMessage", "Cập nhật thất bại!");
        verify(redirectAttributes).addFlashAttribute("toastType", "error");
    }

    @Test
    @DisplayName("UTCID07: Update Category with NULL Parent Category")
    void testUpdateCategory_NullParentCategory() {
        // Arrange
        Long categoryId = 1L;
        String name = "Thức ăn";
        Long parentId = null;
        
        // Mock service to return true (success)
        when(categoryService.updateCategory(categoryId, name, parentId)).thenReturn(true);

        // Act
        String result = categoryController.updateCategory(categoryId, name, parentId, redirectAttributes);

        // Assert
        assertEquals("redirect:/seller/category-dashboard", result);
        verify(categoryService).updateCategory(categoryId, name, parentId);
        verify(redirectAttributes).addFlashAttribute("toastMessage", "Cập nhật thành công!");
        verify(redirectAttributes).addFlashAttribute("toastType", "success");
    }
}
