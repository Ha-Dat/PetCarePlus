package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.dto.RegisterDTO;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.Profile;
import org.example.petcareplus.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerRegisterTest {

    @Mock
    private AccountService accountService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AccountController accountController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
        SecurityContextHolder.clearContext();
    }

    // ========== REGISTER() METHOD TEST CASES ==========
    
    @Test
    @DisplayName("UTCID01: Register with NULL Full Name")
    void testRegister_NullFullName_Status400() {
        // Arrange
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setName(null);
        registerDTO.setPhone("0123456789");
        registerDTO.setPassword("Ab@123456");
        registerDTO.setConfirmPassword("Ab@123456");

        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String result = accountController.register(registerDTO, bindingResult, redirectAttributes, session, model);

        // Assert
        assertEquals("register", result);
        verify(model).addAttribute("error", "Họ tên phải từ 2 đến 50 ký tự!");
        verify(accountService, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("UTCID02: Register with Invalid Phone Number")
    void testRegister_InvalidPhoneNumber_Status400() {
        // Arrange
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setName("Pet");
        registerDTO.setPhone("invalid-phone"); // Invalid format
        registerDTO.setPassword("Ab@123456");
        registerDTO.setConfirmPassword("Ab@123456");

        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String result = accountController.register(registerDTO, bindingResult, redirectAttributes, session, model);

        // Assert
        assertEquals("register", result);
        verify(accountService, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("UTCID03: Register with Password less than 8 characters")
    void testRegister_ShortPassword_Status400() {
        // Arrange
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setName("Pet");
        registerDTO.setPhone("0123456789");
        registerDTO.setPassword("short"); // Less than 8 characters
        registerDTO.setConfirmPassword("short");

        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String result = accountController.register(registerDTO, bindingResult, redirectAttributes, session, model);

        // Assert
        assertEquals("register", result);
        verify(accountService, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("UTCID04: Register with Mismatched Passwords")
    void testRegister_MismatchedPasswords_Status400() {
        // Arrange
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setName("Pet");
        registerDTO.setPhone("0123456789");
        registerDTO.setPassword("Ab@123456");
        registerDTO.setConfirmPassword("DifferentPassword");

        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String result = accountController.register(registerDTO, bindingResult, redirectAttributes, session, model);

        // Assert
        assertEquals("register", result);
        verify(model).addAttribute("error", "Mật khẩu xác nhận không khớp!");
        verify(accountService, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("UTCID05: Register with Existing Phone Number")
    void testRegister_ExistingPhoneNumber_Status400() {
        // Arrange
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setName("Pet");
        registerDTO.setPhone("0123456789");
        registerDTO.setPassword("Ab@123456");
        registerDTO.setConfirmPassword("Ab@123456");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(accountService.isPhoneExists("0123456789")).thenReturn(true);

        // Act
        String result = accountController.register(registerDTO, bindingResult, redirectAttributes, session, model);

        // Assert
        assertEquals("register", result);
        verify(model).addAttribute("error", "Số điện thoại đã tồn tại!");
        verify(accountService, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("UTCID06: Successful Registration")
    void testRegister_SuccessfulRegistration_Status200() {
        // Arrange
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setName("Pet");
        registerDTO.setPhone("0123456789");
        registerDTO.setPassword("Ab@123456");
        registerDTO.setConfirmPassword("Ab@123456");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(accountService.isPhoneExists("0123456789")).thenReturn(false);
        when(accountService.getProfileByAccountAccountId(any())).thenReturn(null);
        when(accountService.profileSave(any(Profile.class))).thenReturn(new Profile());

        // Act
        String result = accountController.register(registerDTO, bindingResult, redirectAttributes, session, model);

        // Assert
        assertEquals("redirect:/login", result);
        verify(accountService).save(any(Account.class));
        verify(accountService).profileSave(any(Profile.class));
        verify(redirectAttributes).addFlashAttribute("message", "Đăng ký thành công! Hãy đăng nhập.");
    }

    @Test
    @DisplayName("UTCID07: Registration when Server is Unavailable")
    void testRegister_ServerUnavailable_Status500() {
        // Arrange
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setName("Pet");
        registerDTO.setPhone("0123456789");
        registerDTO.setPassword("Ab@123456");
        registerDTO.setConfirmPassword("Ab@123456");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(accountService.isPhoneExists("0123456789")).thenThrow(new RuntimeException("Server unavailable"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            accountController.register(registerDTO, bindingResult, redirectAttributes, session, model);
        });
        
        verify(accountService, never()).save(any(Account.class));
    }
}
