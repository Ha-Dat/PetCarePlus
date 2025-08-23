package org.example.petcareplus.controller;

import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.Product;
import org.example.petcareplus.enums.AccountRole;
import org.example.petcareplus.enums.AccountStatus;
import org.example.petcareplus.enums.ProductStatus;
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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CartControllerAddToCartTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartController cartController;

    private MockHttpSession session;
    private Account testAccount;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        testAccount = new Account();
        testAccount.setAccountId(1L);
        testAccount.setName("testuser");
        testAccount.setPhone("0123456789");
        testAccount.setRole(AccountRole.CUSTOMER);
        testAccount.setPassword("password123");
        testAccount.setStatus(AccountStatus.ACTIVE);
        
        testProduct = new Product();
        testProduct.setProductId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Product Description");
        testProduct.setPrice(BigDecimal.valueOf(100));
        testProduct.setUnitInStock(10);
        testProduct.setUnitSold(0);
        testProduct.setStatus(ProductStatus.IN_STOCK);
        testProduct.setCreatedDate(new java.util.Date());
        
        // Set up session with logged in user
        session.setAttribute("loggedInUser", testAccount);
    }

    // ========== ADDTOCART() METHOD TEST CASES ==========

    @Test
    @DisplayName("CID01: Add to Cart with NULL ProductId")
    void testAddToCart_NullProductId() {
        // Arrange
        Long productId = null;
        int quantity = 1;
        when(productService.getProductById(productId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = cartController.addToCart(productId, quantity, session, null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Sản phẩm không tồn tại", response.getBody());
        verify(productService).getProductById(productId);
    }

    @Test
    @DisplayName("CID02: Add to Cart Successfully")
    void testAddToCart_Success() {
        // Arrange
        Long productId = 1L;
        int quantity = 1;
        when(productService.getProductById(productId)).thenReturn(Optional.of(testProduct));

        // Act
        ResponseEntity<?> response = cartController.addToCart(productId, quantity, session, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody()); // Controller returns ResponseEntity.ok().build() which has null body
        verify(productService).getProductById(productId);
        
        // Verify cart was updated in session
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        assertNotNull(cart);
        assertEquals(1, cart.get(productId));
    }

    @Test
    @DisplayName("CID03: Add to Cart with Large Quantity")
    void testAddToCart_LargeQuantity() {
        // Arrange
        Long productId = 1L;
        int quantity = 1000;
        when(productService.getProductById(productId)).thenReturn(Optional.of(testProduct));

        // Act
        ResponseEntity<?> response = cartController.addToCart(productId, quantity, session, null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Không đủ hàng trong kho. Số lượng tối đa có thể thêm: 10", response.getBody());
        verify(productService).getProductById(productId);
    }

    @Test
    @DisplayName("CID04: Add to Cart with Non-existent ProductId")
    void testAddToCart_NonExistentProductId() {
        // Arrange
        Long productId = 1000L;
        int quantity = 1;
        when(productService.getProductById(productId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = cartController.addToCart(productId, quantity, session, null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Sản phẩm không tồn tại", response.getBody());
        verify(productService).getProductById(productId);
    }

    @Test
    @DisplayName("CID05: Add to Cart with Invalid ProductId Format")
    void testAddToCart_InvalidProductIdFormat() {
        // Arrange
        Long productId = -1L; // Invalid format
        int quantity = 1;
        when(productService.getProductById(productId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = cartController.addToCart(productId, quantity, session, null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Sản phẩm không tồn tại", response.getBody());
        verify(productService).getProductById(productId);
    }

    @Test
    @DisplayName("CID06: Add to Cart with Negative Quantity")
    void testAddToCart_NegativeQuantity() {
        // Arrange
        Long productId = 1L;
        int quantity = -1;
        when(productService.getProductById(productId)).thenReturn(Optional.of(testProduct));

        // Act
        ResponseEntity<?> response = cartController.addToCart(productId, quantity, session, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Note: Controller doesn't validate negative quantity, it only checks stock
        verify(productService).getProductById(productId);
        
        // Verify cart was updated with negative quantity
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        assertNotNull(cart);
        assertEquals(-1, cart.get(productId));
    }

    @Test
    @DisplayName("CID07: Add to Cart with Zero Quantity")
    void testAddToCart_ZeroQuantity() {
        // Arrange
        Long productId = 1L;
        int quantity = 0; // Zero quantity
        when(productService.getProductById(productId)).thenReturn(Optional.of(testProduct));

        // Act
        ResponseEntity<?> response = cartController.addToCart(productId, quantity, session, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Note: Controller doesn't validate quantity format, it only checks stock
        verify(productService).getProductById(productId);
        
        // Verify cart was updated with zero quantity
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        assertNotNull(cart);
        assertEquals(0, cart.get(productId));
    }

    @Test
    @DisplayName("CID08: Add to Cart with Unauthenticated User")
    void testAddToCart_UnauthenticatedUser() {
        // Arrange
        Long productId = 1L;
        int quantity = 1;
        session.removeAttribute("loggedInUser"); // Remove user from session

        // Act
        ResponseEntity<?> response = cartController.addToCart(productId, quantity, session, null);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Bạn cần đăng nhập", response.getBody());
        verify(productService, never()).getProductById(any());
    }

    @Test
    @DisplayName("CID09: Add to Cart with 401 Unauthorized")
    void testAddToCart_Unauthorized() {
        // Arrange
        Long productId = 1L;
        int quantity = 1;
        session.removeAttribute("loggedInUser");

        // Act
        ResponseEntity<?> response = cartController.addToCart(productId, quantity, session, null);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Bạn cần đăng nhập", response.getBody());
        verify(productService, never()).getProductById(any());
    }

    @Test
    @DisplayName("CID10: Add to Cart with Server Error")
    void testAddToCart_ServerError() {
        // Arrange
        Long productId = 1L;
        int quantity = 1;
        when(productService.getProductById(productId))
            .thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            cartController.addToCart(productId, quantity, session, null);
        });
        verify(productService).getProductById(productId);
    }

    @Test
    @DisplayName("CID11: Add to Cart with Bad Request")
    void testAddToCart_BadRequest() {
        // Arrange
        Long productId = 1L;
        int quantity = 1;
        when(productService.getProductById(productId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = cartController.addToCart(productId, quantity, session, null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Sản phẩm không tồn tại", response.getBody());
        verify(productService).getProductById(productId);
    }
}
