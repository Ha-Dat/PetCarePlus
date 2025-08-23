package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.dto.LoginDTO;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.enums.AccountRole;
import org.example.petcareplus.enums.AccountStatus;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerLoginTest {

    @Mock
    private AccountService accountService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

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

    // ========== LOGIN() METHOD TEST CASES ==========
    
    @Test
    @DisplayName("UTCID01: Login with NULL phone and NULL password")
    void testLogin_NullPhoneAndNullPassword() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setPhone(null);
        loginDTO.setPassword(null);

        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String result = accountController.login(loginDTO, bindingResult, session, model);

        // Assert
        assertEquals("login", result);
        verify(accountService, never()).login(anyString(), anyString());
    }

    @Test
    @DisplayName("UTCID02: Login with correct phone but wrong password")
    void testLogin_CorrectPhoneWrongPassword() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setPhone("0123456789");
        loginDTO.setPassword("WrongPassword");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(accountService.login("0123456789", "WrongPassword"))
                .thenReturn(Optional.empty());

        // Act
        String result = accountController.login(loginDTO, bindingResult, session, model);

        // Assert
        assertEquals("login", result);
        verify(model).addAttribute("error", "Sai tài khoản hoặc mật khẩu");
        verify(session, never()).setAttribute(anyString(), any());
    }

    @Test
    @DisplayName("UTCID03: Login with phone number not exist in database")
    void testLogin_PhoneNumberNotExist() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setPhone("9999999999");
        loginDTO.setPassword("Password123");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(accountService.login("9999999999", "Password123"))
                .thenReturn(Optional.empty());

        // Act
        String result = accountController.login(loginDTO, bindingResult, session, model);

        // Assert
        assertEquals("login", result);
        verify(model).addAttribute("error", "Sai tài khoản hoặc mật khẩu");
        verify(session, never()).setAttribute(anyString(), any());
    }

    @Test
    @DisplayName("UTCID04: Login with CUSTOMER role - ACTIVE status")
    void testLogin_CustomerRoleActive() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setPhone("0123456789");
        loginDTO.setPassword("Password123");

        Account account = new Account();
        account.setAccountId(1L);
        account.setPhone("0123456789");
        account.setPassword("hashedPassword");
        account.setRole(AccountRole.CUSTOMER);
        account.setStatus(AccountStatus.ACTIVE);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(accountService.login("0123456789", "Password123"))
                .thenReturn(Optional.of(account));

        // Act
        String result = accountController.login(loginDTO, bindingResult, session, model);

        // Assert
        assertEquals("redirect:/home", result);
        verify(session).setAttribute("loggedInUser", account);
        verify(session).setAttribute(eq("SPRING_SECURITY_CONTEXT"), any());
    }

    @Test
    @DisplayName("UTCID05: Login with SELLER role - ACTIVE status")
    void testLogin_SellerRoleActive() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setPhone("0123456789");
        loginDTO.setPassword("Password123");

        Account account = new Account();
        account.setAccountId(1L);
        account.setPhone("0123456789");
        account.setPassword("hashedPassword");
        account.setRole(AccountRole.SELLER);
        account.setStatus(AccountStatus.ACTIVE);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(accountService.login("0123456789", "Password123"))
                .thenReturn(Optional.of(account));

        // Act
        String result = accountController.login(loginDTO, bindingResult, session, model);

        // Assert
        assertEquals("redirect:/seller/dashboard", result);
        verify(session).setAttribute("loggedInUser", account);
    }

    @Test
    @DisplayName("UTCID06: Login with VET role - ACTIVE status")
    void testLogin_VetRoleActive() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setPhone("0123456789");
        loginDTO.setPassword("Password123");

        Account account = new Account();
        account.setAccountId(1L);
        account.setPhone("0123456789");
        account.setPassword("hashedPassword");
        account.setRole(AccountRole.VET);
        account.setStatus(AccountStatus.ACTIVE);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(accountService.login("0123456789", "Password123"))
                .thenReturn(Optional.of(account));

        // Act
        String result = accountController.login(loginDTO, bindingResult, session, model);

        // Assert
        assertEquals("redirect:vet/appointment/pending", result);
        verify(session).setAttribute("loggedInUser", account);
    }

    @Test
    @DisplayName("UTCID07: Login with MANAGER role - ACTIVE status")
    void testLogin_ManagerRoleActive() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setPhone("0123456789");
        loginDTO.setPassword("Password123");

        Account account = new Account();
        account.setAccountId(1L);
        account.setPhone("0123456789");
        account.setPassword("hashedPassword");
        account.setRole(AccountRole.MANAGER);
        account.setStatus(AccountStatus.ACTIVE);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(accountService.login("0123456789", "Password123"))
                .thenReturn(Optional.of(account));

        // Act
        String result = accountController.login(loginDTO, bindingResult, session, model);

        // Assert
        assertEquals("redirect:/manager/service-dashboard", result);
        verify(session).setAttribute("loggedInUser", account);
    }

    @Test
    @DisplayName("UTCID08: Login with ADMIN role - ACTIVE status")
    void testLogin_AdminRoleActive() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setPhone("0123456789");
        loginDTO.setPassword("Password123");

        Account account = new Account();
        account.setAccountId(1L);
        account.setPhone("0123456789");
        account.setPassword("hashedPassword");
        account.setRole(AccountRole.ADMIN);
        account.setStatus(AccountStatus.ACTIVE);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(accountService.login("0123456789", "Password123"))
                .thenReturn(Optional.of(account));

        // Act
        String result = accountController.login(loginDTO, bindingResult, session, model);

        // Assert
        assertEquals("redirect:/admin/dashboard", result);
        verify(session).setAttribute("loggedInUser", account);
    }

    @Test
    @DisplayName("UTCID09: Login with PET_GROOMER role - ACTIVE status")
    void testLogin_PetGroomerRoleActive() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setPhone("0123456789");
        loginDTO.setPassword("Password123");

        Account account = new Account();
        account.setAccountId(1L);
        account.setPhone("0123456789");
        account.setPassword("hashedPassword");
        account.setRole(AccountRole.PET_GROOMER);
        account.setStatus(AccountStatus.ACTIVE);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(accountService.login("0123456789", "Password123"))
                .thenReturn(Optional.of(account));

        // Act
        String result = accountController.login(loginDTO, bindingResult, session, model);

        // Assert
        assertEquals("redirect:/pet-groomer/dashboard", result);
        verify(session).setAttribute("loggedInUser", account);
    }

    @Test
    @DisplayName("UTCID10: Login with BANNED status")
    void testLogin_BannedAccount() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setPhone("0123456789");
        loginDTO.setPassword("Password123");

        Account account = new Account();
        account.setAccountId(1L);
        account.setPhone("0123456789");
        account.setPassword("hashedPassword");
        account.setRole(AccountRole.CUSTOMER);
        account.setStatus(AccountStatus.BANNED);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(accountService.login("0123456789", "Password123"))
                .thenReturn(Optional.of(account));

        // Act
        String result = accountController.login(loginDTO, bindingResult, session, model);

        // Assert
        assertEquals("login", result);
        verify(model).addAttribute("error", "Tài khoản bị khóa!");
        verify(session, never()).setAttribute(anyString(), any());
    }

    @Test
    @DisplayName("UTCID11: Login with server connection exception")
    void testLogin_ServerConnectionException() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setPhone("0123456789");
        loginDTO.setPassword("Password123");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(accountService.login("0123456789", "Password123"))
                .thenThrow(new RuntimeException("Server unavailable"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            accountController.login(loginDTO, bindingResult, session, model);
        });
        
        verify(session, never()).setAttribute(anyString(), any());
    }
}
