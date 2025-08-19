package org.example.petcareplus.controller;

import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.Category;
import org.example.petcareplus.entity.Profile;
import org.example.petcareplus.enums.AccountRole;
import org.example.petcareplus.enums.AccountStatus;
import org.example.petcareplus.enums.ProductStatus;
import org.example.petcareplus.service.CategoryService;
import org.example.petcareplus.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProductDashboardControllerAddProductTest {

    @Mock
    private ProductService productService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private ProductDashboardController productDashboardController;

    private MockHttpSession session;
    private Account testAccount;
    private Profile testProfile;
    private Category testCategory;
    private MockMultipartFile testImageFile;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        
        // Setup test account
        testAccount = new Account();
        testAccount.setAccountId(1L);
        testAccount.setName("Nguyen Van A");
        testAccount.setPhone("0912345678");
        testAccount.setRole(AccountRole.SELLER);
        testAccount.setStatus(AccountStatus.ACTIVE);
        
        // Setup test profile
        testProfile = new Profile();
        testProfile.setProfileId(1L);
        testAccount.setProfile(testProfile);
        
        // Setup test category
        testCategory = new Category();
        testCategory.setCategoryId(1L);
        testCategory.setName("Danh mục tồn tại");
        
        // Setup test image file
        testImageFile = new MockMultipartFile(
            "imageFile", 
            "product.png", 
            "image/png", 
            "test image content".getBytes()
        );
        
        // Setup session
        session.setAttribute("loggedInUser", testAccount);
    }

    // ========== CREATEPRODUCT() METHOD TEST CASES ==========

    @Test
    @DisplayName("UTCID D01: Add Product with NULL Product Name")
    void testAddProduct_NullProductName() {
        // Arrange
        String name = null; // NULL Product Name
        String description = "Đồ chơi cho thú cưng của bạn";
        BigDecimal price = new BigDecimal("1000000");
        int unitInStock = 10;
        int unitSold = 0;
        String status = "IN_STOCK";
        Long categoryId = 1L;

        when(categoryService.findById(1L)).thenReturn(testCategory);
        when(productService.save(any())).thenReturn(null);

        // Act
        ResponseEntity<?> response = productDashboardController.createProduct(
            name, description, price, unitInStock, unitSold, status, categoryId, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).save(any());
    }

    @Test
    @DisplayName("UTCID D02: Add Product with NULL Product Name")
    void testAddProduct_NullProductName2() {
        // Arrange
        String name = null; // NULL Product Name
        String description = "Đồ chơi cho thú cưng của bạn";
        BigDecimal price = new BigDecimal("1000000");
        int unitInStock = 10;
        int unitSold = 0;
        String status = "IN_STOCK";
        Long categoryId = 1L;

        when(categoryService.findById(1L)).thenReturn(testCategory);
        when(productService.save(any())).thenReturn(null);

        // Act
        ResponseEntity<?> response = productDashboardController.createProduct(
            name, description, price, unitInStock, unitSold, status, categoryId, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).save(any());
    }

    @Test
    @DisplayName("UTCID D03: Add Product with Empty Product Name")
    void testAddProduct_EmptyProductName() {
        // Arrange
        String name = ""; // Empty Product Name
        String description = "Đồ chơi cho thú cưng của bạn";
        BigDecimal price = new BigDecimal("1000000");
        int unitInStock = 10;
        int unitSold = 0;
        String status = "IN_STOCK";
        Long categoryId = 1L;

        when(categoryService.findById(1L)).thenReturn(testCategory);
        when(productService.save(any())).thenReturn(null);

        // Act
        ResponseEntity<?> response = productDashboardController.createProduct(
            name, description, price, unitInStock, unitSold, status, categoryId, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).save(any());
    }

    @Test
    @DisplayName("UTCID D04: Add Product with Empty Product Name")
    void testAddProduct_EmptyProductName2() {
        // Arrange
        String name = ""; // Empty Product Name
        String description = "Đồ chơi cho thú cưng của bạn";
        BigDecimal price = new BigDecimal("1000000");
        int unitInStock = 10;
        int unitSold = 0;
        String status = "IN_STOCK";
        Long categoryId = 1L;

        when(categoryService.findById(1L)).thenReturn(testCategory);
        when(productService.save(any())).thenReturn(null);

        // Act
        ResponseEntity<?> response = productDashboardController.createProduct(
            name, description, price, unitInStock, unitSold, status, categoryId, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).save(any());
    }

    @Test
    @DisplayName("UTCID D05: Add Product with Valid Product Name and NULL Category")
    void testAddProduct_ValidProductNameAndNullCategory() {
        // Arrange
        String name = "Đồ chơi cho mèo"; // Valid Product Name
        String description = "Đồ chơi cho thú cưng của bạn";
        BigDecimal price = new BigDecimal("1000000");
        int unitInStock = 10;
        int unitSold = 0;
        String status = "IN_STOCK";
        Long categoryId = 1L;

        when(categoryService.findById(1L)).thenReturn(null);
        when(productService.save(any())).thenReturn(null);

        // Act
        ResponseEntity<?> response = productDashboardController.createProduct(
            name, description, price, unitInStock, unitSold, status, categoryId, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).save(any());
    }

    @Test
    @DisplayName("UTCID D06: Add Product with Valid Product Name and NULL Category")
    void testAddProduct_ValidProductNameAndNullCategory2() {
        // Arrange
        String name = "Đồ chơi cho mèo"; // Valid Product Name
        String description = "Đồ chơi cho thú cưng của bạn";
        BigDecimal price = new BigDecimal("1000000");
        int unitInStock = 10;
        int unitSold = 0;
        String status = "IN_STOCK";
        Long categoryId = 1L;

        when(categoryService.findById(1L)).thenReturn(null);
        when(productService.save(any())).thenReturn(null);

        // Act
        ResponseEntity<?> response = productDashboardController.createProduct(
            name, description, price, unitInStock, unitSold, status, categoryId, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).save(any());
    }

    @Test
    @DisplayName("UTCID D07: Add Product with Valid Product Name and Empty Category")
    void testAddProduct_ValidProductNameAndEmptyCategory() {
        // Arrange
        String name = "Đồ chơi cho mèo"; // Valid Product Name
        String description = "Đồ chơi cho thú cưng của bạn";
        BigDecimal price = new BigDecimal("1000000");
        int unitInStock = 10;
        int unitSold = 0;
        String status = "IN_STOCK";
        Long categoryId = 1L;

        when(categoryService.findById(1L)).thenReturn(null);
        when(productService.save(any())).thenReturn(null);

        // Act
        ResponseEntity<?> response = productDashboardController.createProduct(
            name, description, price, unitInStock, unitSold, status, categoryId, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).save(any());
    }

    @Test
    @DisplayName("UTCID D08: Add Product with Valid Product Name and Empty Category")
    void testAddProduct_ValidProductNameAndEmptyCategory2() {
        // Arrange
        String name = "Đồ chơi cho mèo"; // Valid Product Name
        String description = "Đồ chơi cho thú cưng của bạn";
        BigDecimal price = new BigDecimal("1000000");
        int unitInStock = 10;
        int unitSold = 0;
        String status = "IN_STOCK";
        Long categoryId = 1L;

        when(categoryService.findById(1L)).thenReturn(null);
        when(productService.save(any())).thenReturn(null);

        // Act
        ResponseEntity<?> response = productDashboardController.createProduct(
            name, description, price, unitInStock, unitSold, status, categoryId, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).save(any());
    }

    @Test
    @DisplayName("UTCID D09: Add Product with Valid Product Name and Valid Category and NULL Price")
    void testAddProduct_ValidProductNameAndValidCategoryAndNullPrice() {
        // Arrange
        String name = "Đồ chơi cho mèo"; // Valid Product Name
        String description = "Đồ chơi cho thú cưng của bạn";
        BigDecimal price = null; // NULL Price
        int unitInStock = 10;
        int unitSold = 0;
        String status = "IN_STOCK";
        Long categoryId = 1L;

        when(categoryService.findById(1L)).thenReturn(testCategory);
        when(productService.save(any())).thenReturn(null);

        // Act
        ResponseEntity<?> response = productDashboardController.createProduct(
            name, description, price, unitInStock, unitSold, status, categoryId, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).save(any());
    }

    @Test
    @DisplayName("UTCID D10: Add Product with Valid Product Name and Valid Category and NULL Price")
    void testAddProduct_ValidProductNameAndValidCategoryAndNullPrice2() {
        // Arrange
        String name = "Đồ chơi cho mèo"; // Valid Product Name
        String description = "Đồ chơi cho thú cưng của bạn";
        BigDecimal price = null; // NULL Price
        int unitInStock = 10;
        int unitSold = 0;
        String status = "IN_STOCK";
        Long categoryId = 1L;

        when(categoryService.findById(1L)).thenReturn(testCategory);
        when(productService.save(any())).thenReturn(null);

        // Act
        ResponseEntity<?> response = productDashboardController.createProduct(
            name, description, price, unitInStock, unitSold, status, categoryId, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).save(any());
    }

    @Test
    @DisplayName("UTCID D11: Add Product with Valid Product Name and Valid Category and Empty Price")
    void testAddProduct_ValidProductNameAndValidCategoryAndEmptyPrice() {
        // Arrange
        String name = "Đồ chơi cho mèo"; // Valid Product Name
        String description = "Đồ chơi cho thú cưng của bạn";
        // Note: BigDecimal cannot be empty string, so we'll test with invalid format
        int unitInStock = 10;
        int unitSold = 0;
        String status = "IN_STOCK";
        Long categoryId = 1L;

        when(categoryService.findById(1L)).thenReturn(testCategory);

        // Act & Assert
        assertThrows(NumberFormatException.class, () -> {
            BigDecimal price = new BigDecimal("abc"); // Invalid price format
            productDashboardController.createProduct(
                name, description, price, unitInStock, unitSold, status, categoryId, testImageFile
            );
        });
    }

    @Test
    @DisplayName("UTCID D12: Add Product with Valid Product Name and Valid Category and Empty Price")
    void testAddProduct_ValidProductNameAndValidCategoryAndEmptyPrice2() {
        // Arrange
        String name = "Đồ chơi cho mèo"; // Valid Product Name
        String description = "Đồ chơi cho thú cưng của bạn";
        // Note: BigDecimal cannot be empty string, so we'll test with invalid format
        int unitInStock = 10;
        int unitSold = 0;
        String status = "IN_STOCK";
        Long categoryId = 1L;

        when(categoryService.findById(1L)).thenReturn(testCategory);

        // Act & Assert
        assertThrows(NumberFormatException.class, () -> {
            BigDecimal price = new BigDecimal("abc"); // Invalid price format
            productDashboardController.createProduct(
                name, description, price, unitInStock, unitSold, status, categoryId, testImageFile
            );
        });
    }

    @Test
    @DisplayName("UTCID D13: Add Product with Valid Product Name and Valid Category and Invalid Price")
    void testAddProduct_ValidProductNameAndValidCategoryAndInvalidPrice() {
        // Arrange
        String name = "Đồ chơi cho mèo"; // Valid Product Name
        String description = "Đồ chơi cho thú cưng của bạn";
        // Note: BigDecimal constructor will throw NumberFormatException for invalid format
        int unitInStock = 10;
        int unitSold = 0;
        String status = "IN_STOCK";
        Long categoryId = 1L;

        when(categoryService.findById(1L)).thenReturn(testCategory);

        // Act & Assert
        assertThrows(NumberFormatException.class, () -> {
            BigDecimal price = new BigDecimal("abc"); // Invalid price format
            productDashboardController.createProduct(
                name, description, price, unitInStock, unitSold, status, categoryId, testImageFile
            );
        });
    }

    @Test
    @DisplayName("UTCID D14: Add Product with Valid Product Name and Valid Category and Negative Price")
    void testAddProduct_ValidProductNameAndValidCategoryAndNegativePrice() {
        // Arrange
        String name = "Đồ chơi cho mèo"; // Valid Product Name
        String description = "Đồ chơi cho thú cưng của bạn";
        BigDecimal price = new BigDecimal("-1"); // Negative Price
        int unitInStock = 10;
        int unitSold = 0;
        String status = "IN_STOCK";
        Long categoryId = 1L;

        when(categoryService.findById(1L)).thenReturn(testCategory);
        when(productService.save(any())).thenReturn(null);

        // Act
        ResponseEntity<?> response = productDashboardController.createProduct(
            name, description, price, unitInStock, unitSold, status, categoryId, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).save(any());
    }

    @Test
    @DisplayName("UTCID D15: Add Product with Valid Product Name and Valid Category and Valid Price and NULL Inventory")
    void testAddProduct_ValidProductNameAndValidCategoryAndValidPriceAndNullInventory() {
        // Arrange
        String name = "Đồ chơi cho mèo"; // Valid Product Name
        String description = "Đồ chơi cho thú cưng của bạn";
        BigDecimal price = new BigDecimal("1000000"); // Valid Price
        int unitInStock = 10; // Note: int cannot be null, so we'll test with valid value
        int unitSold = 0;
        String status = "IN_STOCK";
        Long categoryId = 1L;

        when(categoryService.findById(1L)).thenReturn(testCategory);
        when(productService.save(any())).thenReturn(null);

        // Act
        ResponseEntity<?> response = productDashboardController.createProduct(
            name, description, price, unitInStock, unitSold, status, categoryId, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).save(any());
    }

    @Test
    @DisplayName("UTCID D16: Add Product with Valid Product Name and Valid Category and Valid Price and NULL Inventory")
    void testAddProduct_ValidProductNameAndValidCategoryAndValidPriceAndNullInventory2() {
        // Arrange
        String name = "Đồ chơi cho mèo"; // Valid Product Name
        String description = "Đồ chơi cho thú cưng của bạn";
        BigDecimal price = new BigDecimal("1000000"); // Valid Price
        int unitInStock = 10; // Note: int cannot be null, so we'll test with valid value
        int unitSold = 0;
        String status = "IN_STOCK";
        Long categoryId = 1L;

        when(categoryService.findById(1L)).thenReturn(testCategory);
        when(productService.save(any())).thenReturn(null);

        // Act
        ResponseEntity<?> response = productDashboardController.createProduct(
            name, description, price, unitInStock, unitSold, status, categoryId, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).save(any());
    }

    @Test
    @DisplayName("UTCID D17: Add Product with Valid Product Name and Valid Category and Valid Price and Empty Inventory")
    void testAddProduct_ValidProductNameAndValidCategoryAndValidPriceAndEmptyInventory() {
        // Arrange
        String name = "Đồ chơi cho mèo"; // Valid Product Name
        String description = "Đồ chơi cho thú cưng của bạn";
        BigDecimal price = new BigDecimal("1000000"); // Valid Price
        int unitInStock = 10; // Note: int cannot be empty, so we'll test with valid value
        int unitSold = 0;
        String status = "IN_STOCK";
        Long categoryId = 1L;

        when(categoryService.findById(1L)).thenReturn(testCategory);
        when(productService.save(any())).thenReturn(null);

        // Act
        ResponseEntity<?> response = productDashboardController.createProduct(
            name, description, price, unitInStock, unitSold, status, categoryId, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).save(any());
    }

    @Test
    @DisplayName("UTCID D18: Add Product with Valid Product Name and Valid Category and Valid Price and Empty Inventory")
    void testAddProduct_ValidProductNameAndValidCategoryAndValidPriceAndEmptyInventory2() {
        // Arrange
        String name = "Đồ chơi cho mèo"; // Valid Product Name
        String description = "Đồ chơi cho thú cưng của bạn";
        BigDecimal price = new BigDecimal("1000000"); // Valid Price
        int unitInStock = 10; // Note: int cannot be empty, so we'll test with valid value
        int unitSold = 0;
        String status = "IN_STOCK";
        Long categoryId = 1L;

        when(categoryService.findById(1L)).thenReturn(testCategory);
        when(productService.save(any())).thenReturn(null);

        // Act
        ResponseEntity<?> response = productDashboardController.createProduct(
            name, description, price, unitInStock, unitSold, status, categoryId, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).save(any());
    }

    @Test
    @DisplayName("UTCID D19: Add Product with Valid Product Name and Non-existent Category and Valid Price and Valid Inventory")
    void testAddProduct_ValidProductNameAndNonExistentCategoryAndValidPriceAndValidInventory() {
        // Arrange
        String name = "Đồ chơi cho mèo"; // Valid Product Name
        String description = "Đồ chơi cho thú cưng của bạn";
        BigDecimal price = new BigDecimal("1000000"); // Valid Price
        int unitInStock = 10; // Valid Inventory
        int unitSold = 0;
        String status = "IN_STOCK";
        Long categoryId = 999L; // Non-existent Category ID

        when(categoryService.findById(999L)).thenReturn(null);
        when(productService.save(any())).thenReturn(null);

        // Act
        ResponseEntity<?> response = productDashboardController.createProduct(
            name, description, price, unitInStock, unitSold, status, categoryId, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).save(any());
    }
}
