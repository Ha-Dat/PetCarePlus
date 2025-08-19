package org.example.petcareplus.controller;

import org.example.petcareplus.service.ResetPasswordService;
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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ResetPasswordControllerValidateResetPasswordInfoTest {

    @Mock
    private ResetPasswordService resetPasswordService;

    @InjectMocks
    private ResetPasswordController resetPasswordController;

    // ========== VALIDATERESETPASSWORDINFO() METHOD TEST CASES ==========

    @Test
    @DisplayName("UTCID01: Validate with Correct Name and Phone")
    void testValidateResetPasswordInfo_CorrectNameAndPhone() {
        // Arrange
        String username = "testuser";
        String phoneNumber = "0123456789";
        when(resetPasswordService.validateUserCredentials(username, phoneNumber)).thenReturn(true);

        // Act
        ResponseEntity<?> response = resetPasswordController.validateResetPasswordInfo(username, phoneNumber);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(true, responseBody.get("success"));
        assertEquals("Thông tin hợp lệ, có thể đặt lại mật khẩu", responseBody.get("message"));
        assertEquals(username, responseBody.get("username"));
        assertEquals(phoneNumber, responseBody.get("phoneNumber"));
        verify(resetPasswordService).validateUserCredentials(username, phoneNumber);
    }

    @Test
    @DisplayName("UTCID02: Validate with NULL Phone")
    void testValidateResetPasswordInfo_NullPhone() {
        // Arrange
        String username = "testuser";
        String phoneNumber = null;

        // Act
        ResponseEntity<?> response = resetPasswordController.validateResetPasswordInfo(username, phoneNumber);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("success"));
        assertEquals("Số điện thoại không được để trống", responseBody.get("message"));
        verify(resetPasswordService, never()).validateUserCredentials(anyString(), anyString());
    }

    @Test
    @DisplayName("UTCID03: Validate with Incorrect Name")
    void testValidateResetPasswordInfo_IncorrectName() {
        // Arrange
        String username = "wronguser";
        String phoneNumber = "0123456789";
        when(resetPasswordService.validateUserCredentials(username, phoneNumber)).thenReturn(false);

        // Act
        ResponseEntity<?> response = resetPasswordController.validateResetPasswordInfo(username, phoneNumber);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("success"));
        assertEquals("Họ tên hoặc số điện thoại không khớp với thông tin đăng ký. Vui lòng kiểm tra lại.", responseBody.get("message"));
        verify(resetPasswordService).validateUserCredentials(username, phoneNumber);
    }

    @Test
    @DisplayName("UTCID04: Validate with Non-existent Phone")
    void testValidateResetPasswordInfo_NonExistentPhone() {
        // Arrange
        String username = "testuser";
        String phoneNumber = "9999999999";
        when(resetPasswordService.validateUserCredentials(username, phoneNumber)).thenReturn(false);

        // Act
        ResponseEntity<?> response = resetPasswordController.validateResetPasswordInfo(username, phoneNumber);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("success"));
        assertEquals("Họ tên hoặc số điện thoại không khớp với thông tin đăng ký. Vui lòng kiểm tra lại.", responseBody.get("message"));
        verify(resetPasswordService).validateUserCredentials(username, phoneNumber);
    }

    @Test
    @DisplayName("UTCID05: Validate with Server Error")
    void testValidateResetPasswordInfo_ServerError() {
        // Arrange
        String username = "testuser";
        String phoneNumber = "0123456789";
        when(resetPasswordService.validateUserCredentials(username, phoneNumber))
            .thenThrow(new RuntimeException("Database connection failed"));

        // Act
        ResponseEntity<?> response = resetPasswordController.validateResetPasswordInfo(username, phoneNumber);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("Lỗi hệ thống"));
        verify(resetPasswordService).validateUserCredentials(username, phoneNumber);
    }

    @Test
    @DisplayName("UTCID06: Validate with NULL Username")
    void testValidateResetPasswordInfo_NullUsername() {
        // Arrange
        String username = null;
        String phoneNumber = "0123456789";

        // Act
        ResponseEntity<?> response = resetPasswordController.validateResetPasswordInfo(username, phoneNumber);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("success"));
        assertEquals("Tên đăng nhập không được để trống", responseBody.get("message"));
        verify(resetPasswordService, never()).validateUserCredentials(anyString(), anyString());
    }
}
