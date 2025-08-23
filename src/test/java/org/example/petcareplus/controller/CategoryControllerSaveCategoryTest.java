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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CategoryControllerSaveCategoryTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private CategoryController categoryController;

    private MockHttpSession session;
    private Account testAccount;
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
        
        testParentCategory = new Category();
        testParentCategory.setCategoryId(1L);
        testParentCategory.setName("Thức ăn");
        
        // Set up session with logged in user
        session.setAttribute("loggedInUser", testAccount);
    }

    // ========== SAVECATEGORY() METHOD TEST CASES ==========

    @Test
    @DisplayName("UTCID01: Save Category with NULL Name")
    void testSaveCategory_NullName() {
        // Arrange
        String name = null;
        Long parentId = 1L;
        
        // Mock service to throw exception for null name
        doThrow(new IllegalArgumentException("Tên danh mục không được bỏ trống"))
            .when(categoryService).saveCategory(name, parentId);

        // Act
        String result = categoryController.saveCategory(name, parentId, redirectAttributes);

        // Assert
        assertEquals("redirect:/seller/category-dashboard", result);
        verify(categoryService).saveCategory(name, parentId);
        verify(redirectAttributes).addFlashAttribute("toastMessage", "Thêm danh mục thất bại!");
        verify(redirectAttributes).addFlashAttribute("toastType", "error");
    }

    @Test
    @DisplayName("UTCID02: Save Category Successfully")
    void testSaveCategory_Success() {
        // Arrange
        String name = "Thức ăn";
        Long parentId = null;

        // Act
        String result = categoryController.saveCategory(name, parentId, redirectAttributes);

        // Assert
        assertEquals("redirect:/seller/category-dashboard", result);
        verify(categoryService).saveCategory(name, parentId);
        verify(redirectAttributes).addFlashAttribute("toastMessage", "Thêm danh mục thành công!");
        verify(redirectAttributes).addFlashAttribute("toastType", "success");
    }

    @Test
    @DisplayName("UTCID03: Save Category with Empty Name")
    void testSaveCategory_EmptyName() {
        // Arrange
        String name = "";
        Long parentId = 1L;
        
        // Mock service to throw exception for empty name
        doThrow(new IllegalArgumentException("Tên danh mục không được bỏ trống"))
            .when(categoryService).saveCategory(name, parentId);

        // Act
        String result = categoryController.saveCategory(name, parentId, redirectAttributes);

        // Assert
        assertEquals("redirect:/seller/category-dashboard", result);
        verify(categoryService).saveCategory(name, parentId);
        verify(redirectAttributes).addFlashAttribute("toastMessage", "Thêm danh mục thất bại!");
        verify(redirectAttributes).addFlashAttribute("toastType", "error");
    }

    @Test
    @DisplayName("UTCID04: Save Category with Valid Parent Category")
    void testSaveCategory_ValidParentCategory() {
        // Arrange
        String name = "Thức ăn";
        Long parentId = 1L;

        // Act
        String result = categoryController.saveCategory(name, parentId, redirectAttributes);

        // Assert
        assertEquals("redirect:/seller/category-dashboard", result);
        verify(categoryService).saveCategory(name, parentId);
        verify(redirectAttributes).addFlashAttribute("toastMessage", "Thêm danh mục thành công!");
        verify(redirectAttributes).addFlashAttribute("toastType", "success");
    }

    @Test
    @DisplayName("UTCID05: Save Category with Non-existent Parent Category")
    void testSaveCategory_NonExistentParentCategory() {
        // Arrange
        String name = "Thức ăn";
        Long parentId = 999L; // Non-existent parent
        
        // Mock service to throw exception for non-existent parent
        doThrow(new IllegalArgumentException("danh mục cha không tồn tại"))
            .when(categoryService).saveCategory(name, parentId);

        // Act
        String result = categoryController.saveCategory(name, parentId, redirectAttributes);

        // Assert
        assertEquals("redirect:/seller/category-dashboard", result);
        verify(categoryService).saveCategory(name, parentId);
        verify(redirectAttributes).addFlashAttribute("toastMessage", "Thêm danh mục thất bại!");
        verify(redirectAttributes).addFlashAttribute("toastType", "error");
    }

    @Test
    @DisplayName("UTCID06: Save Category with Non-existent Parent Category in Database")
    void testSaveCategory_NonExistentParentCategoryInDatabase() {
        // Arrange
        String name = "Thức ăn";
        Long parentId = 999L; // Non-existent parent in database
        
        // Mock service to throw exception for non-existent parent in database
        doThrow(new IllegalArgumentException("danh mục cha không tồn tại"))
            .when(categoryService).saveCategory(name, parentId);

        // Act
        String result = categoryController.saveCategory(name, parentId, redirectAttributes);

        // Assert
        assertEquals("redirect:/seller/category-dashboard", result);
        verify(categoryService).saveCategory(name, parentId);
        verify(redirectAttributes).addFlashAttribute("toastMessage", "Thêm danh mục thất bại!");
        verify(redirectAttributes).addFlashAttribute("toastType", "error");
    }

    @Test
    @DisplayName("UTCID07: Save Category with NULL Parent Category")
    void testSaveCategory_NullParentCategory() {
        // Arrange
        String name = "Thức ăn";
        Long parentId = null;

        // Act
        String result = categoryController.saveCategory(name, parentId, redirectAttributes);

        // Assert
        assertEquals("redirect:/seller/category-dashboard", result);
        verify(categoryService).saveCategory(name, parentId);
        verify(redirectAttributes).addFlashAttribute("toastMessage", "Thêm danh mục thành công!");
        verify(redirectAttributes).addFlashAttribute("toastType", "success");
    }
}
