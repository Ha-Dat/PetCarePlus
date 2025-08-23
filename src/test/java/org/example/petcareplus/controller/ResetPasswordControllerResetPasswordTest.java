package org.example.petcareplus.controller;

import org.example.petcareplus.dto.ResetPasswordDTO;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ResetPasswordControllerResetPasswordTest {

    @Mock
    private ResetPasswordService resetPasswordService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private ResetPasswordController resetPasswordController;

    private ResetPasswordDTO resetPasswordDTO;

    @BeforeEach
    void setUp() {
        resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setUsername("testuser");
        resetPasswordDTO.setPhoneNumber("0123456789");
        resetPasswordDTO.setNewPassword("Abc1@123");
        resetPasswordDTO.setConfirmPassword("Abc1@123");
    }

    // ========== RESETPASSWORD() METHOD TEST CASES ==========

    @Test
    @DisplayName("UTCID01: Reset with NULL Token")
    void testResetPassword_NullToken() {
        // Arrange
        resetPasswordDTO.setUsername(null);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.Collections.singletonList(
            new FieldError("resetPasswordDTO", "username", "Tên đăng nhập không được để trống")
        ));

        // Act
        ResponseEntity<?> response = resetPasswordController.resetPassword(resetPasswordDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("username"));
    }

    @Test
    @DisplayName("UTCID02: Reset with NULL New Password")
    void testResetPassword_NullNewPassword() {
        // Arrange
        resetPasswordDTO.setNewPassword(null);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.Collections.singletonList(
            new FieldError("resetPasswordDTO", "newPassword", "Mật khẩu mới không được để trống")
        ));

        // Act
        ResponseEntity<?> response = resetPasswordController.resetPassword(resetPasswordDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("newPassword"));
    }

    @Test
    @DisplayName("UTCID03: Successful Password Reset")
    void testResetPassword_SuccessfulReset() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);
        when(resetPasswordService.resetPassword(any(ResetPasswordDTO.class))).thenReturn(true);

        // Act
        ResponseEntity<?> response = resetPasswordController.resetPassword(resetPasswordDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(true, responseBody.get("success"));
        assertEquals("Đặt lại mật khẩu thành công", responseBody.get("message"));
        assertEquals("testuser", responseBody.get("username"));
        verify(resetPasswordService).resetPassword(any(ResetPasswordDTO.class));
    }

    @Test
    @DisplayName("UTCID04: Reset with Short Password")
    void testResetPassword_ShortPassword() {
        // Arrange
        resetPasswordDTO.setNewPassword("123");
        resetPasswordDTO.setConfirmPassword("123");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.Collections.singletonList(
            new FieldError("resetPasswordDTO", "newPassword", "Mật khẩu phải có ít nhất 8 và ít hơn 50 ký tự")
        ));

        // Act
        ResponseEntity<?> response = resetPasswordController.resetPassword(resetPasswordDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("8"));
    }

    @Test
    @DisplayName("UTCID05: Reset with Long Password")
    void testResetPassword_LongPassword() {
        // Arrange
        String longPassword = "A".repeat(51) + "b1@";
        resetPasswordDTO.setNewPassword(longPassword);
        resetPasswordDTO.setConfirmPassword(longPassword);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.Collections.singletonList(
            new FieldError("resetPasswordDTO", "newPassword", "Mật khẩu phải có ít nhất 8 và ít hơn 50 ký tự")
        ));

        // Act
        ResponseEntity<?> response = resetPasswordController.resetPassword(resetPasswordDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("50"));
    }

    @Test
    @DisplayName("UTCID06: Reset with Missing Uppercase")
    void testResetPassword_MissingUppercase() {
        // Arrange
        resetPasswordDTO.setNewPassword("abc123@123");
        resetPasswordDTO.setConfirmPassword("abc123@123");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.Collections.singletonList(
            new FieldError("resetPasswordDTO", "newPassword", "Mật khẩu phải chứa ít nhất 1 chữ cái in hoa")
        ));

        // Act
        ResponseEntity<?> response = resetPasswordController.resetPassword(resetPasswordDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("in hoa"));
    }

    @Test
    @DisplayName("UTCID07: Reset with Missing Lowercase")
    void testResetPassword_MissingLowercase() {
        // Arrange
        resetPasswordDTO.setNewPassword("ABC123@123");
        resetPasswordDTO.setConfirmPassword("ABC123@123");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.Collections.singletonList(
            new FieldError("resetPasswordDTO", "newPassword", "Mật khẩu phải chứa ít nhất 1 chữ cái in thường")
        ));

        // Act
        ResponseEntity<?> response = resetPasswordController.resetPassword(resetPasswordDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("in thường"));
    }

    @Test
    @DisplayName("UTCID08: Reset with Missing Special Character")
    void testResetPassword_MissingSpecialCharacter() {
        // Arrange
        resetPasswordDTO.setNewPassword("Abc12345");
        resetPasswordDTO.setConfirmPassword("Abc12345");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.Collections.singletonList(
            new FieldError("resetPasswordDTO", "newPassword", "Mật khẩu phải chứa ít nhất 1 kí tự đặc biệt")
        ));

        // Act
        ResponseEntity<?> response = resetPasswordController.resetPassword(resetPasswordDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("kí tự đặc biệt"));
    }

    @Test
    @DisplayName("UTCID09: Reset with Missing Digit")
    void testResetPassword_MissingDigit() {
        // Arrange
        resetPasswordDTO.setNewPassword("Abc@@@@@");
        resetPasswordDTO.setConfirmPassword("Abc@@@@@");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.Collections.singletonList(
            new FieldError("resetPasswordDTO", "newPassword", "Mật khẩu phải chứa ít nhất 1 chữ số")
        ));

        // Act
        ResponseEntity<?> response = resetPasswordController.resetPassword(resetPasswordDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("chữ số"));
    }

    @Test
    @DisplayName("UTCID10: Reset with Mismatched Passwords")
    void testResetPassword_MismatchedPasswords() {
        // Arrange
        resetPasswordDTO.setNewPassword("Abc1@123");
        resetPasswordDTO.setConfirmPassword("DifferentPassword123@");
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        ResponseEntity<?> response = resetPasswordController.resetPassword(resetPasswordDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("success"));
        assertEquals("Mật khẩu xác nhận không khớp", responseBody.get("message"));
        verify(resetPasswordService, never()).resetPassword(any(ResetPasswordDTO.class));
    }

    @Test
    @DisplayName("UTCID11: Reset with Service Failure")
    void testResetPassword_ServiceFailure() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);
        when(resetPasswordService.resetPassword(any(ResetPasswordDTO.class))).thenReturn(false);

        // Act
        ResponseEntity<?> response = resetPasswordController.resetPassword(resetPasswordDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("success"));
        assertEquals("Không thể đặt lại mật khẩu. Vui lòng kiểm tra thông tin", responseBody.get("message"));
        verify(resetPasswordService).resetPassword(any(ResetPasswordDTO.class));
    }

    @Test
    @DisplayName("UTCID12: Reset with Server Error")
    void testResetPassword_ServerError() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);
        when(resetPasswordService.resetPassword(any(ResetPasswordDTO.class)))
            .thenThrow(new RuntimeException("Database connection failed"));

        // Act
        ResponseEntity<?> response = resetPasswordController.resetPassword(resetPasswordDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("Lỗi hệ thống"));
        verify(resetPasswordService).resetPassword(any(ResetPasswordDTO.class));
    }
}
