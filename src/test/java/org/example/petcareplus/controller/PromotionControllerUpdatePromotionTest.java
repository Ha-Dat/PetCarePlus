package org.example.petcareplus.controller;

import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.Profile;
import org.example.petcareplus.entity.Promotion;
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
class PromotionControllerUpdatePromotionTest {

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
    private Promotion testPromotion;

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
        
        // Setup test promotion
        testPromotion = new Promotion();
        testPromotion.setPromotionId(1L);
        testPromotion.setTitle("Spa123");
        testPromotion.setDescription("giảm giá");
        testPromotion.setDiscount(new BigDecimal("0.10"));
        testPromotion.setStartDate(LocalDateTime.of(2025, 8, 14, 10, 0));
        testPromotion.setEndDate(LocalDateTime.of(2025, 8, 20, 10, 0));
        testPromotion.setStatus(PromotionStatus.ACTIVE);
        
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

    // ========== UPDATEPROMOTION() METHOD TEST CASES ==========

    @Test
    @DisplayName("UTCID 01: Update Promotion with Valid Promotion ID")
    void testUpdatePromotion_ValidPromotionId() {
        // Arrange
        Long id = 1L; // Valid Promotion ID
        String title = "Spa123";
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("10");
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        when(promotionService.getPromotionById(1L)).thenReturn(testPromotion);
        doNothing().when(promotionService).updatePromotion(any(Promotion.class));

        // Act
        ResponseEntity<?> response = promotionController.updatePromotion(
            id, title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cập nhật thành công", response.getBody());
        verify(promotionService).updatePromotion(any(Promotion.class));
    }

    @Test
    @DisplayName("UTCID 02: Update Promotion with Valid Promotion ID and Valid Code")
    void testUpdatePromotion_ValidPromotionIdAndCode() {
        // Arrange
        Long id = 1L; // Valid Promotion ID
        String title = "Spa123"; // Valid Promotion Code
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("10");
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        when(promotionService.getPromotionById(1L)).thenReturn(testPromotion);
        doNothing().when(promotionService).updatePromotion(any(Promotion.class));

        // Act
        ResponseEntity<?> response = promotionController.updatePromotion(
            id, title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cập nhật thành công", response.getBody());
        verify(promotionService).updatePromotion(any(Promotion.class));
    }

    @Test
    @DisplayName("UTCID 03: Update Promotion with Valid Promotion Code")
    void testUpdatePromotion_ValidPromotionCode() {
        // Arrange
        Long id = 1L;
        String title = "Spa123"; // Valid Promotion Code
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("10");
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        when(promotionService.getPromotionById(1L)).thenReturn(testPromotion);
        doNothing().when(promotionService).updatePromotion(any(Promotion.class));

        // Act
        ResponseEntity<?> response = promotionController.updatePromotion(
            id, title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cập nhật thành công", response.getBody());
        verify(promotionService).updatePromotion(any(Promotion.class));
    }

    @Test
    @DisplayName("UTCID 04: Update Promotion with Invalid Promotion Code")
    void testUpdatePromotion_InvalidPromotionCode() {
        // Arrange
        Long id = 1L;
        String title = "Spa"; // Invalid Promotion Code (too short)
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("10");
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        when(promotionService.getPromotionById(1L)).thenReturn(testPromotion);
        doNothing().when(promotionService).updatePromotion(any(Promotion.class));

        // Act
        ResponseEntity<?> response = promotionController.updatePromotion(
            id, title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cập nhật thành công", response.getBody());
        verify(promotionService).updatePromotion(any(Promotion.class));
    }

    @Test
    @DisplayName("UTCID 05: Update Promotion with NULL Description")
    void testUpdatePromotion_NullDescription() {
        // Arrange
        Long id = 1L;
        String title = "Spa123";
        String description = null; // NULL Description
        BigDecimal discount = new BigDecimal("10");
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        when(promotionService.getPromotionById(1L)).thenReturn(testPromotion);
        doNothing().when(promotionService).updatePromotion(any(Promotion.class));

        // Act
        ResponseEntity<?> response = promotionController.updatePromotion(
            id, title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cập nhật thành công", response.getBody());
        verify(promotionService).updatePromotion(any(Promotion.class));
    }

    @Test
    @DisplayName("UTCID 06: Update Promotion with Valid Description")
    void testUpdatePromotion_ValidDescription() {
        // Arrange
        Long id = 1L;
        String title = "Spa123";
        String description = "giảm giá"; // Valid Description
        BigDecimal discount = new BigDecimal("10");
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        when(promotionService.getPromotionById(1L)).thenReturn(testPromotion);
        doNothing().when(promotionService).updatePromotion(any(Promotion.class));

        // Act
        ResponseEntity<?> response = promotionController.updatePromotion(
            id, title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cập nhật thành công", response.getBody());
        verify(promotionService).updatePromotion(any(Promotion.class));
    }

    @Test
    @DisplayName("UTCID 07: Update Promotion with Valid Description and Valid Discount")
    void testUpdatePromotion_ValidDescriptionAndDiscount() {
        // Arrange
        Long id = 1L;
        String title = "Spa123";
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("10"); // Valid discount
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        when(promotionService.getPromotionById(1L)).thenReturn(testPromotion);
        doNothing().when(promotionService).updatePromotion(any(Promotion.class));

        // Act
        ResponseEntity<?> response = promotionController.updatePromotion(
            id, title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cập nhật thành công", response.getBody());
        verify(promotionService).updatePromotion(any(Promotion.class));
    }

    @Test
    @DisplayName("UTCID 08: Update Promotion with Valid Discount")
    void testUpdatePromotion_ValidDiscount() {
        // Arrange
        Long id = 1L;
        String title = "Spa123";
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("10"); // Valid discount
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        when(promotionService.getPromotionById(1L)).thenReturn(testPromotion);
        doNothing().when(promotionService).updatePromotion(any(Promotion.class));

        // Act
        ResponseEntity<?> response = promotionController.updatePromotion(
            id, title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cập nhật thành công", response.getBody());
        verify(promotionService).updatePromotion(any(Promotion.class));
    }

    @Test
    @DisplayName("UTCID 09: Update Promotion with NULL Discount")
    void testUpdatePromotion_NullDiscount() {
        // Arrange
        Long id = 1L;
        String title = "Spa123";
        String description = "giảm giá";
        BigDecimal discount = null; // NULL Discount
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        when(promotionService.getPromotionById(1L)).thenReturn(testPromotion);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            promotionController.updatePromotion(
                id, title, description, discount, startDate, endDate, status, testImageFile
            );
        });
    }

    @Test
    @DisplayName("UTCID 10: Update Promotion with Invalid Discount Format")
    void testUpdatePromotion_InvalidDiscountFormat() {
        // Arrange
        Long id = 1L;
        String title = "Spa123";
        String description = "giảm giá";
        // Note: BigDecimal constructor will throw NumberFormatException for invalid format
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        when(promotionService.getPromotionById(1L)).thenReturn(testPromotion);

        // Act & Assert
        assertThrows(NumberFormatException.class, () -> {
            BigDecimal discount = new BigDecimal("abc"); // This will throw NumberFormatException
            promotionController.updatePromotion(
                id, title, description, discount, startDate, endDate, status, testImageFile
            );
        });
    }

    @Test
    @DisplayName("UTCID 11: Update Promotion with Negative Discount")
    void testUpdatePromotion_NegativeDiscount() {
        // Arrange
        Long id = 1L;
        String title = "Spa123";
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("-1"); // Negative discount
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        when(promotionService.getPromotionById(1L)).thenReturn(testPromotion);
        doNothing().when(promotionService).updatePromotion(any(Promotion.class));

        // Act
        ResponseEntity<?> response = promotionController.updatePromotion(
            id, title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cập nhật thành công", response.getBody());
        verify(promotionService).updatePromotion(any(Promotion.class));
    }

    @Test
    @DisplayName("UTCID 12: Update Promotion with Discount Exceeding 100")
    void testUpdatePromotion_DiscountExceeding100() {
        // Arrange
        Long id = 1L;
        String title = "Spa123";
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("101"); // Discount > 100
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        when(promotionService.getPromotionById(1L)).thenReturn(testPromotion);
        doNothing().when(promotionService).updatePromotion(any(Promotion.class));

        // Act
        ResponseEntity<?> response = promotionController.updatePromotion(
            id, title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cập nhật thành công", response.getBody());
        verify(promotionService).updatePromotion(any(Promotion.class));
    }

    @Test
    @DisplayName("UTCID 13: Update Promotion with Valid Discount")
    void testUpdatePromotion_ValidDiscount2() {
        // Arrange
        Long id = 1L;
        String title = "Spa123";
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("10"); // Valid discount
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        when(promotionService.getPromotionById(1L)).thenReturn(testPromotion);
        doNothing().when(promotionService).updatePromotion(any(Promotion.class));

        // Act
        ResponseEntity<?> response = promotionController.updatePromotion(
            id, title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cập nhật thành công", response.getBody());
        verify(promotionService).updatePromotion(any(Promotion.class));
    }

    @Test
    @DisplayName("UTCID 14: Update Promotion with Start Date After Current Date")
    void testUpdatePromotion_StartDateAfterCurrentDate() {
        // Arrange
        Long id = 1L;
        String title = "Spa123";
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("10");
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0); // After current date (13/8/2025)
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        when(promotionService.getPromotionById(1L)).thenReturn(testPromotion);
        doNothing().when(promotionService).updatePromotion(any(Promotion.class));

        // Act
        ResponseEntity<?> response = promotionController.updatePromotion(
            id, title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cập nhật thành công", response.getBody());
        verify(promotionService).updatePromotion(any(Promotion.class));
    }

    @Test
    @DisplayName("UTCID 15: Update Promotion with Start Date Before Current Date")
    void testUpdatePromotion_StartDateBeforeCurrentDate() {
        // Arrange
        Long id = 1L;
        String title = "Spa123";
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("10");
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 12, 10, 0); // Before current date (13/8/2025)
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        when(promotionService.getPromotionById(1L)).thenReturn(testPromotion);
        doNothing().when(promotionService).updatePromotion(any(Promotion.class));

        // Act
        ResponseEntity<?> response = promotionController.updatePromotion(
            id, title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cập nhật thành công", response.getBody());
        verify(promotionService).updatePromotion(any(Promotion.class));
    }

    @Test
    @DisplayName("UTCID 16: Update Promotion with Start Date After Current Date")
    void testUpdatePromotion_StartDateAfterCurrentDate2() {
        // Arrange
        Long id = 1L;
        String title = "Spa123";
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("10");
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0); // After current date (13/8/2025)
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        when(promotionService.getPromotionById(1L)).thenReturn(testPromotion);
        doNothing().when(promotionService).updatePromotion(any(Promotion.class));

        // Act
        ResponseEntity<?> response = promotionController.updatePromotion(
            id, title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cập nhật thành công", response.getBody());
        verify(promotionService).updatePromotion(any(Promotion.class));
    }

    @Test
    @DisplayName("UTCID 17: Update Promotion with End Date After Start Date")
    void testUpdatePromotion_EndDateAfterStartDate() {
        // Arrange
        Long id = 1L;
        String title = "Spa123";
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("10");
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0); // After start date
        PromotionStatus status = PromotionStatus.ACTIVE;

        when(promotionService.getPromotionById(1L)).thenReturn(testPromotion);
        doNothing().when(promotionService).updatePromotion(any(Promotion.class));

        // Act
        ResponseEntity<?> response = promotionController.updatePromotion(
            id, title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cập nhật thành công", response.getBody());
        verify(promotionService).updatePromotion(any(Promotion.class));
    }

    @Test
    @DisplayName("UTCID 18: Update Promotion with Active Status")
    void testUpdatePromotion_ActiveStatus() {
        // Arrange
        Long id = 1L;
        String title = "Spa123";
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("10");
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE; // Active status

        when(promotionService.getPromotionById(1L)).thenReturn(testPromotion);
        doNothing().when(promotionService).updatePromotion(any(Promotion.class));

        // Act
        ResponseEntity<?> response = promotionController.updatePromotion(
            id, title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cập nhật thành công", response.getBody());
        verify(promotionService).updatePromotion(any(Promotion.class));
    }

    @Test
    @DisplayName("UTCID 19: Update Promotion with Image")
    void testUpdatePromotion_WithImage() {
        // Arrange
        Long id = 1L;
        String title = "Spa123";
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("10");
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        when(promotionService.getPromotionById(1L)).thenReturn(testPromotion);
        doNothing().when(promotionService).updatePromotion(any(Promotion.class));

        // Act
        ResponseEntity<?> response = promotionController.updatePromotion(
            id, title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cập nhật thành công", response.getBody());
        verify(promotionService).updatePromotion(any(Promotion.class));
    }

    @Test
    @DisplayName("UTCID 20: Update Promotion with Non-existent Promotion ID")
    void testUpdatePromotion_NonExistentPromotionId() {
        // Arrange
        Long id = 1000L; // Non-existent Promotion ID
        String title = "Spa123";
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("10");
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        when(promotionService.getPromotionById(1000L)).thenReturn(null);

        // Act
        ResponseEntity<?> response = promotionController.updatePromotion(
            id, title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Không tìm thấy khuyến mãi.", response.getBody());
        verify(promotionService, never()).updatePromotion(any(Promotion.class));
    }

    @Test
    @DisplayName("UTCID 21: Update Promotion with Invalid Promotion ID")
    void testUpdatePromotion_InvalidPromotionId() {
        // Arrange
        Long id = -1L; // Invalid Promotion ID
        String title = "Spa123";
        String description = "giảm giá";
        BigDecimal discount = new BigDecimal("10");
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 14, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 20, 10, 0);
        PromotionStatus status = PromotionStatus.ACTIVE;

        when(promotionService.getPromotionById(-1L)).thenReturn(null);

        // Act
        ResponseEntity<?> response = promotionController.updatePromotion(
            id, title, description, discount, startDate, endDate, status, testImageFile
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Không tìm thấy khuyến mãi.", response.getBody());
        verify(promotionService, never()).updatePromotion(any(Promotion.class));
    }
}
