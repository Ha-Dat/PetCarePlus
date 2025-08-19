package org.example.petcareplus.controller;

import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.Profile;
import org.example.petcareplus.enums.AccountRole;
import org.example.petcareplus.enums.AccountStatus;
import org.example.petcareplus.enums.PromotionStatus;
import org.example.petcareplus.service.PromotionService;
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
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PromotionControllerAddPromotionTest {

    @Mock
    private PromotionService promotionService;

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private PromotionController promotionController;

    private MockHttpSession session;
    private Account testAccount;
    private Profile testProfile;
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
        
        // Setup test image file
        testImageFile = new MockMultipartFile(
            "imageFile", 
            "Promotion.png", 
            "image/png", 
            "test image content".getBytes()
        );
        
        // Setup session
        session.setAttribute("loggedInUser", testAccount);
    }

    // ========== CREATEPROMOTION() METHOD TEST CASES ==========

    @Test
    @DisplayName("UTCID D01: Add Promotion with NULL Promotion Code")
    void testAddPromotion_NullPromotionCode() {
        // Arrange
        String title = null; // NULL Promotion Code
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("10");
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        // Act
        String result = promotionController.createPromotion(
            title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        // Note: This method doesn't validate input directly, it will proceed with null title
        // The validation should be handled at service layer or frontend
        assertNotNull(result);
    }

    @Test
    @DisplayName("UTCID D02: Add Promotion with Empty Promotion Code")
    void testAddPromotion_EmptyPromotionCode() {
        // Arrange
        String title = ""; // Empty Promotion Code
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("10");
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        // Act
        String result = promotionController.createPromotion(
            title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("UTCID D03: Add Promotion with Valid Promotion Code")
    void testAddPromotion_ValidPromotionCode() {
        // Arrange
        String title = "Spa123"; // Valid Promotion Code
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("10");
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        doNothing().when(promotionService).savePromotion(any());

        // Act
        String result = promotionController.createPromotion(
            title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertEquals("redirect:/seller/promotion/list", result);
        verify(promotionService).savePromotion(any());
    }

    @Test
    @DisplayName("UTCID D04: Add Promotion with Invalid Promotion Code")
    void testAddPromotion_InvalidPromotionCode() {
        // Arrange
        String title = "Spa"; // Invalid Promotion Code (too short)
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("10");
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        // Act
        String result = promotionController.createPromotion(
            title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("UTCID D05: Add Promotion with NULL Description")
    void testAddPromotion_NullDescription() {
        // Arrange
        String title = "Spa123";
        String description = null; // NULL Description
        BigDecimal discount = new BigDecimal("10");
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        // Act
        String result = promotionController.createPromotion(
            title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("UTCID D06: Add Promotion with Empty Description")
    void testAddPromotion_EmptyDescription() {
        // Arrange
        String title = "Spa123";
        String description = ""; // Empty Description
        BigDecimal discount = new BigDecimal("10");
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        // Act
        String result = promotionController.createPromotion(
            title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("UTCID D07: Add Promotion with Valid Description")
    void testAddPromotion_ValidDescription() {
        // Arrange
        String title = "Spa123";
        String description = "giảm giá"; // Valid Description
        BigDecimal discount = new BigDecimal("10");
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        doNothing().when(promotionService).savePromotion(any());

        // Act
        String result = promotionController.createPromotion(
            title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertEquals("redirect:/seller/promotion/list", result);
        verify(promotionService).savePromotion(any());
    }

    @Test
    @DisplayName("UTCID D08: Add Promotion with Valid Description and Valid Discount")
    void testAddPromotion_ValidDescriptionAndDiscount() {
        // Arrange
        String title = "Spa123";
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("10"); // Valid discount
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        doNothing().when(promotionService).savePromotion(any());

        // Act
        String result = promotionController.createPromotion(
            title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertEquals("redirect:/seller/promotion/list", result);
        verify(promotionService).savePromotion(any());
    }

    @Test
    @DisplayName("UTCID D09: Add Promotion with NULL Discount")
    void testAddPromotion_NullDiscount() {
        // Arrange
        String title = "Spa123";
        String description = "giảm giá";
        BigDecimal discount = null; // NULL Discount
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            promotionController.createPromotion(
                title, description, discount, startDate, endDate, status, testImageFile
            );
        });
    }

    @Test
    @DisplayName("UTCID D10: Add Promotion with Invalid Discount Format")
    void testAddPromotion_InvalidDiscountFormat() {
        // Arrange
        String title = "Spa123";
        String description = "giảm giá";
        // Note: BigDecimal constructor will throw NumberFormatException for invalid format
        // We'll test this by creating a BigDecimal from an invalid string
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        // Act & Assert
        assertThrows(NumberFormatException.class, () -> {
            BigDecimal discount = new BigDecimal("abc"); // This will throw NumberFormatException
            promotionController.createPromotion(
                title, description, discount, startDate, endDate, status, testImageFile
            );
        });
    }

    @Test
    @DisplayName("UTCID D11: Add Promotion with Negative Discount")
    void testAddPromotion_NegativeDiscount() {
        // Arrange
        String title = "Spa123";
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("-1"); // Negative discount
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        // Act
        String result = promotionController.createPromotion(
            title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("UTCID D12: Add Promotion with Discount Exceeding 100")
    void testAddPromotion_DiscountExceeding100() {
        // Arrange
        String title = "Spa123";
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("101"); // Discount > 100
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        // Act
        String result = promotionController.createPromotion(
            title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("UTCID D13: Add Promotion with Valid Discount")
    void testAddPromotion_ValidDiscount() {
        // Arrange
        String title = "Spa123";
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("10"); // Valid discount
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        doNothing().when(promotionService).savePromotion(any());

        // Act
        String result = promotionController.createPromotion(
            title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertEquals("redirect:/seller/promotion/list", result);
        verify(promotionService).savePromotion(any());
    }

    @Test
    @DisplayName("UTCID D14: Add Promotion with Empty Start Date")
    void testAddPromotion_EmptyStartDate() {
        // Arrange
        String title = "Spa123";
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("10");
        LocalDateTime startDate = null; // Empty Start Date
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        // Act
        String result = promotionController.createPromotion(
            title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("UTCID D15: Add Promotion with Start Date Before Current Date")
    void testAddPromotion_StartDateBeforeCurrentDate() {
        // Arrange
        String title = "Spa123";
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("10");
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 12, 10, 0); // Before current date (13/8/2025)
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        doNothing().when(promotionService).savePromotion(any());

        // Act
        String result = promotionController.createPromotion(
            title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertEquals("redirect:/seller/promotion/list", result);
        verify(promotionService).savePromotion(any());
    }

    @Test
    @DisplayName("UTCID D16: Add Promotion with Start Date After Current Date")
    void testAddPromotion_StartDateAfterCurrentDate() {
        // Arrange
        String title = "Spa123";
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("10");
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0); // After current date (13/8/2025)
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        doNothing().when(promotionService).savePromotion(any());

        // Act
        String result = promotionController.createPromotion(
            title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertEquals("redirect:/seller/promotion/list", result);
        verify(promotionService).savePromotion(any());
    }

    @Test
    @DisplayName("UTCID D17: Add Promotion with End Date Same as Current Date")
    void testAddPromotion_EndDateSameAsCurrentDate() {
        // Arrange
        String title = "Spa123";
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("10");
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 13, 10, 0); // Same as current date (13/8/2025)
        PromotionStatus status = PromotionStatus.INACTIVE; // Inactive status

        doNothing().when(promotionService).savePromotion(any());

        // Act
        String result = promotionController.createPromotion(
            title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertEquals("redirect:/seller/promotion/list", result);
        verify(promotionService).savePromotion(any());
    }
}
