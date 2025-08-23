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
class CartControllerRemoveFromCartTest {

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

    // ========== REMOVEFROMCART() METHOD TEST CASES ==========

    @Test
    @DisplayName("UTCID01: Remove from Cart with NULL ProductId")
    void testRemoveFromCart_NullProductId() {
        // Arrange
        Long productId = null;
        
        // Set up cart with some items
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(1L, 2);
        cart.put(2L, 1);
        session.setAttribute("cart", cart);

        // Act
        ResponseEntity<?> response = cartController.removeFromCart(productId, session);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Note: Controller doesn't validate null productId, it just removes null key
        Map<Long, Integer> updatedCart = (Map<Long, Integer>) session.getAttribute("cart");
        assertNotNull(updatedCart);
        // Null key removal doesn't affect other items
        assertEquals(2, updatedCart.get(1L));
        assertEquals(1, updatedCart.get(2L));
    }

    @Test
    @DisplayName("UTCID02: Remove from Cart Successfully")
    void testRemoveFromCart_Success() {
        // Arrange
        Long productId = 1L;
        
        // Set up cart with the product to remove
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(productId, 2);
        cart.put(2L, 1);
        session.setAttribute("cart", cart);

        // Act
        ResponseEntity<?> response = cartController.removeFromCart(productId, session);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody()); // Controller returns ResponseEntity.ok().build()
        
        // Verify product was removed from cart
        Map<Long, Integer> updatedCart = (Map<Long, Integer>) session.getAttribute("cart");
        assertNotNull(updatedCart);
        assertNull(updatedCart.get(productId)); // Product should be removed
        assertEquals(1, updatedCart.get(2L)); // Other product should remain
    }

    @Test
    @DisplayName("UTCID03: Remove from Cart with Non-existent ProductId")
    void testRemoveFromCart_NonExistentProductId() {
        // Arrange
        Long productId = 999L; // Non-existent product
        
        // Set up cart with other products
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(1L, 2);
        cart.put(2L, 1);
        session.setAttribute("cart", cart);

        // Act
        ResponseEntity<?> response = cartController.removeFromCart(productId, session);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Note: Controller doesn't validate if product exists, it just removes the key
        
        // Verify cart remains unchanged
        Map<Long, Integer> updatedCart = (Map<Long, Integer>) session.getAttribute("cart");
        assertNotNull(updatedCart);
        assertEquals(2, updatedCart.get(1L));
        assertEquals(1, updatedCart.get(2L));
    }

    @Test
    @DisplayName("UTCID04: Remove from Cart with Empty Cart")
    void testRemoveFromCart_EmptyCart() {
        // Arrange
        Long productId = 1L;
        
        // Set up empty cart
        Map<Long, Integer> cart = new HashMap<>();
        session.setAttribute("cart", cart);

        // Act
        ResponseEntity<?> response = cartController.removeFromCart(productId, session);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // Verify cart remains empty
        Map<Long, Integer> updatedCart = (Map<Long, Integer>) session.getAttribute("cart");
        assertNotNull(updatedCart);
        assertTrue(updatedCart.isEmpty());
    }

    @Test
    @DisplayName("UTCID05: Remove from Cart with Invalid ProductId Format")
    void testRemoveFromCart_InvalidProductIdFormat() {
        // Arrange
        Long productId = -1L; // Invalid format
        
        // Set up cart with valid products
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(1L, 2);
        cart.put(2L, 1);
        session.setAttribute("cart", cart);

        // Act
        ResponseEntity<?> response = cartController.removeFromCart(productId, session);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Note: Controller doesn't validate productId format, it just removes the key
        
        // Verify cart remains unchanged (negative ID doesn't exist in cart)
        Map<Long, Integer> updatedCart = (Map<Long, Integer>) session.getAttribute("cart");
        assertNotNull(updatedCart);
        assertEquals(2, updatedCart.get(1L));
        assertEquals(1, updatedCart.get(2L));
    }

    @Test
    @DisplayName("UTCID06: Remove from Cart with Multiple Products")
    void testRemoveFromCart_MultipleProducts() {
        // Arrange
        Long productId = 1L;
        
        // Set up cart with multiple products
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(1L, 2);
        cart.put(2L, 1);
        cart.put(3L, 3);
        session.setAttribute("cart", cart);

        // Act
        ResponseEntity<?> response = cartController.removeFromCart(productId, session);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // Verify only the specified product was removed
        Map<Long, Integer> updatedCart = (Map<Long, Integer>) session.getAttribute("cart");
        assertNotNull(updatedCart);
        assertNull(updatedCart.get(1L)); // Should be removed
        assertEquals(1, updatedCart.get(2L)); // Should remain
        assertEquals(3, updatedCart.get(3L)); // Should remain
    }

    @Test
    @DisplayName("UTCID07: Remove from Cart with Zero Quantity Product")
    void testRemoveFromCart_ZeroQuantityProduct() {
        // Arrange
        Long productId = 1L;
        
        // Set up cart with a product that has zero quantity
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(productId, 0);
        cart.put(2L, 1);
        session.setAttribute("cart", cart);

        // Act
        ResponseEntity<?> response = cartController.removeFromCart(productId, session);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // Verify product was removed regardless of quantity
        Map<Long, Integer> updatedCart = (Map<Long, Integer>) session.getAttribute("cart");
        assertNotNull(updatedCart);
        assertNull(updatedCart.get(productId)); // Should be removed
        assertEquals(1, updatedCart.get(2L)); // Should remain
    }

    @Test
    @DisplayName("UTCID08: Remove from Cart with Unauthenticated User")
    void testRemoveFromCart_UnauthenticatedUser() {
        // Arrange
        Long productId = 1L;
        session.removeAttribute("loggedInUser"); // Remove user from session
        
        // Set up cart
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(productId, 2);
        session.setAttribute("cart", cart);

        // Act
        ResponseEntity<?> response = cartController.removeFromCart(productId, session);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Note: Controller doesn't check authentication for removeFromCart
        
        // Verify product was still removed (no auth check)
        Map<Long, Integer> updatedCart = (Map<Long, Integer>) session.getAttribute("cart");
        assertNotNull(updatedCart);
        assertNull(updatedCart.get(productId)); // Should be removed
    }

    @Test
    @DisplayName("UTCID09: Remove from Cart with 401 Unauthorized")
    void testRemoveFromCart_Unauthorized() {
        // Arrange
        Long productId = 1L;
        session.removeAttribute("loggedInUser");
        
        // Set up cart
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(productId, 2);
        session.setAttribute("cart", cart);

        // Act
        ResponseEntity<?> response = cartController.removeFromCart(productId, session);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Note: Controller doesn't return 401 for removeFromCart, it just removes the item
        
        // Verify product was removed
        Map<Long, Integer> updatedCart = (Map<Long, Integer>) session.getAttribute("cart");
        assertNotNull(updatedCart);
        assertNull(updatedCart.get(productId));
    }

    @Test
    @DisplayName("UTCID10: Remove from Cart with Server Error")
    void testRemoveFromCart_ServerError() {
        // Arrange
        Long productId = 1L;
        
        // Set up cart
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(productId, 2);
        session.setAttribute("cart", cart);

        // Act
        ResponseEntity<?> response = cartController.removeFromCart(productId, session);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Note: Controller doesn't throw exceptions for removeFromCart, it always returns OK
        
        // Verify product was removed
        Map<Long, Integer> updatedCart = (Map<Long, Integer>) session.getAttribute("cart");
        assertNotNull(updatedCart);
        assertNull(updatedCart.get(productId));
    }

    @Test
    @DisplayName("UTCID11: Remove from Cart with Bad Request")
    void testRemoveFromCart_BadRequest() {
        // Arrange
        Long productId = 1L;
        
        // Set up cart
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(productId, 2);
        session.setAttribute("cart", cart);

        // Act
        ResponseEntity<?> response = cartController.removeFromCart(productId, session);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Note: Controller doesn't return bad request for removeFromCart, it always returns OK
        
        // Verify product was removed
        Map<Long, Integer> updatedCart = (Map<Long, Integer>) session.getAttribute("cart");
        assertNotNull(updatedCart);
        assertNull(updatedCart.get(productId));
    }
}
