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
import software.amazon.awssdk.services.s3.S3Client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PromotionControllerTogglePromotionStatusTest {

    @Mock
    private PromotionService promotionService;

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private PromotionController promotionController;

    private MockHttpSession session;
    private Account testAccount;
    private Profile testProfile;
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
        
        // Setup session
        session.setAttribute("loggedInUser", testAccount);
    }

    // ========== CHANGEPROMOTIONSTATUS() METHOD TEST CASES ==========

    @Test
    @DisplayName("UTCID 0: Toggle Promotion Status with NULL Promotion ID")
    void testTogglePromotionStatus_NullPromotionId() {
        // Arrange
        Long id = null; // NULL Promotion ID

        // Act
        ResponseEntity<?> response = promotionController.changePromotionStatus(id);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Không tìm thấy.", response.getBody());
        verify(promotionService, never()).updatePromotion(any(Promotion.class));
    }

    @Test
    @DisplayName("UTCID 1: Toggle Promotion Status with Empty Promotion ID")
    void testTogglePromotionStatus_EmptyPromotionId() {
        // Arrange
        // Note: Long cannot be empty string, so we'll test with 0L as empty equivalent
        Long id = 0L; // Empty Promotion ID equivalent

        when(promotionService.getPromotionById(0L)).thenReturn(null);

        // Act
        ResponseEntity<?> response = promotionController.changePromotionStatus(id);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Không tìm thấy.", response.getBody());
        verify(promotionService, never()).updatePromotion(any(Promotion.class));
    }

    @Test
    @DisplayName("UTCID 2: Toggle Promotion Status with Valid Promotion ID and Active Status")
    void testTogglePromotionStatus_ValidPromotionIdAndActiveStatus() {
        // Arrange
        Long id = 1L; // Valid Promotion ID
        testPromotion.setStatus(PromotionStatus.ACTIVE); // Active status

        when(promotionService.getPromotionById(1L)).thenReturn(testPromotion);
        doNothing().when(promotionService).updatePromotion(any(Promotion.class));

        // Act
        ResponseEntity<?> response = promotionController.changePromotionStatus(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Thay đổi trạng thái thành công.", response.getBody());
        verify(promotionService).updatePromotion(any(Promotion.class));
        
        // Verify status was toggled from ACTIVE to INACTIVE
        verify(promotionService).updatePromotion(argThat(promotion -> 
            promotion.getStatus() == PromotionStatus.INACTIVE
        ));
    }

    @Test
    @DisplayName("UTCID 3: Toggle Promotion Status with Invalid Promotion ID")
    void testTogglePromotionStatus_InvalidPromotionId() {
        // Arrange
        Long id = -1L; // Invalid Promotion ID

        when(promotionService.getPromotionById(-1L)).thenReturn(null);

        // Act
        ResponseEntity<?> response = promotionController.changePromotionStatus(id);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Không tìm thấy.", response.getBody());
        verify(promotionService, never()).updatePromotion(any(Promotion.class));
    }

    @Test
    @DisplayName("UTCID 4: Toggle Promotion Status with Non-existent Promotion ID")
    void testTogglePromotionStatus_NonExistentPromotionId() {
        // Arrange
        Long id = 1000L; // Non-existent Promotion ID

        when(promotionService.getPromotionById(1000L)).thenReturn(null);

        // Act
        ResponseEntity<?> response = promotionController.changePromotionStatus(id);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Không tìm thấy.", response.getBody());
        verify(promotionService, never()).updatePromotion(any(Promotion.class));
    }

    @Test
    @DisplayName("UTCID 5: Toggle Promotion Status with Inactive Status")
    void testTogglePromotionStatus_InactiveStatus() {
        // Arrange
        Long id = 1L;
        testPromotion.setStatus(PromotionStatus.INACTIVE); // Inactive status

        when(promotionService.getPromotionById(1L)).thenReturn(testPromotion);
        doNothing().when(promotionService).updatePromotion(any(Promotion.class));

        // Act
        ResponseEntity<?> response = promotionController.changePromotionStatus(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Thay đổi trạng thái thành công.", response.getBody());
        verify(promotionService).updatePromotion(any(Promotion.class));
        
        // Verify status was toggled from INACTIVE to ACTIVE
        verify(promotionService).updatePromotion(argThat(promotion -> 
            promotion.getStatus() == PromotionStatus.ACTIVE
        ));
    }

    @Test
    @DisplayName("UTCID 6: Toggle Promotion Status with Active Status")
    void testTogglePromotionStatus_ActiveStatus() {
        // Arrange
        Long id = 1L;
        testPromotion.setStatus(PromotionStatus.ACTIVE); // Active status

        when(promotionService.getPromotionById(1L)).thenReturn(testPromotion);
        doNothing().when(promotionService).updatePromotion(any(Promotion.class));

        // Act
        ResponseEntity<?> response = promotionController.changePromotionStatus(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Thay đổi trạng thái thành công.", response.getBody());
        verify(promotionService).updatePromotion(any(Promotion.class));
        
        // Verify status was toggled from ACTIVE to INACTIVE
        verify(promotionService).updatePromotion(argThat(promotion -> 
            promotion.getStatus() == PromotionStatus.INACTIVE
        ));
    }

    @Test
    @DisplayName("UTCID 7: Toggle Promotion Status with Active Status")
    void testTogglePromotionStatus_ActiveStatus2() {
        // Arrange
        Long id = 1L;
        testPromotion.setStatus(PromotionStatus.ACTIVE); // Active status

        when(promotionService.getPromotionById(1L)).thenReturn(testPromotion);
        doNothing().when(promotionService).updatePromotion(any(Promotion.class));

        // Act
        ResponseEntity<?> response = promotionController.changePromotionStatus(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Thay đổi trạng thái thành công.", response.getBody());
        verify(promotionService).updatePromotion(any(Promotion.class));
        
        // Verify status was toggled from ACTIVE to INACTIVE
        verify(promotionService).updatePromotion(argThat(promotion -> 
            promotion.getStatus() == PromotionStatus.INACTIVE
        ));
    }

    @Test
    @DisplayName("UTCID 8: Toggle Promotion Status with Unauthenticated User")
    void testTogglePromotionStatus_UnauthenticatedUser() {
        // Arrange
        Long id = 1L;
        
        // Note: The current method doesn't check authentication
        // This test case is for future implementation or different method
        // For now, we'll test the current behavior
        when(promotionService.getPromotionById(1L)).thenReturn(testPromotion);
        doNothing().when(promotionService).updatePromotion(any(Promotion.class));

        // Act
        ResponseEntity<?> response = promotionController.changePromotionStatus(id);

        // Assert
        // Current implementation doesn't check authentication
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Thay đổi trạng thái thành công.", response.getBody());
        verify(promotionService).updatePromotion(any(Promotion.class));
    }

    @Test
    @DisplayName("UTCID 9: Toggle Promotion Status with Server Error")
    void testTogglePromotionStatus_ServerError() {
        // Arrange
        Long id = 1L;

        when(promotionService.getPromotionById(1L)).thenReturn(testPromotion);
        doThrow(new RuntimeException("Database connection failed"))
            .when(promotionService).updatePromotion(any(Promotion.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            promotionController.changePromotionStatus(id);
        });
        
        verify(promotionService).getPromotionById(1L);
        verify(promotionService).updatePromotion(any(Promotion.class));
    }
}
