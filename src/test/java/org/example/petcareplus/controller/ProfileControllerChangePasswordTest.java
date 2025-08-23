package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.dto.ChangePasswordDTO;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.service.AccountService;
import org.example.petcareplus.service.CategoryService;
import org.example.petcareplus.service.LocationService;
import org.example.petcareplus.service.ProfileService;
import org.example.petcareplus.util.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProfileControllerChangePasswordTest {

    @Mock
    private ProfileService profileService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private LocationService locationService;

    @Mock
    private AccountService accountService;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Account account;

    @InjectMocks
    private ProfileController profileController;

    @BeforeEach
    void setUp() {
        // Setup default account
        when(session.getAttribute("loggedInUser")).thenReturn(account);
        when(account.getPassword()).thenReturn(PasswordHasher.hash("oldPassword123"));
    }

    // ========== CHANGEPASSWORD() METHOD TEST CASES ==========

    @Test
    @DisplayName("UTCID01: Change with NULL Old Password")
    void testChangePassword_NullOldPassword() {
        // Arrange
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setOldPassword(null);
        changePasswordDTO.setNewPassword("newPassword123");
        changePasswordDTO.setConfirmPassword("newPassword123");

        when(bindingResult.hasErrors()).thenReturn(false);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            profileController.changePassword(changePasswordDTO, bindingResult, session, model);
        });
        
        verify(accountService, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("UTCID02: Change with NULL New Password")
    void testChangePassword_NullNewPassword() {
        // Arrange
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setOldPassword("oldPassword123");
        changePasswordDTO.setNewPassword(null);
        changePasswordDTO.setConfirmPassword("newPassword123");

        when(bindingResult.hasErrors()).thenReturn(false);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            profileController.changePassword(changePasswordDTO, bindingResult, session, model);
        });
        
        verify(accountService, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("UTCID03: Successful Password Change")
    void testChangePassword_SuccessfulChange() {
        // Arrange
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setOldPassword("oldPassword123");
        changePasswordDTO.setNewPassword("newPassword123");
        changePasswordDTO.setConfirmPassword("newPassword123");

        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String result = profileController.changePassword(changePasswordDTO, bindingResult, session, model);

        // Assert
        assertEquals("change-password", result);
        verify(account).setPassword(anyString());
        verify(accountService).save(account);
        verify(model).addAttribute(eq("success"), eq("Đổi mật khẩu thành công!"));
    }

    @Test
    @DisplayName("UTCID04: Change with NULL Confirm Password")
    void testChangePassword_NullConfirmPassword() {
        // Arrange
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setOldPassword("oldPassword123");
        changePasswordDTO.setNewPassword("newPassword123");
        changePasswordDTO.setConfirmPassword(null);

        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String result = profileController.changePassword(changePasswordDTO, bindingResult, session, model);

        // Assert
        assertEquals("change-password", result);
        verify(model).addAttribute(eq("error"), anyString());
        verify(accountService, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("UTCID05: Change with Invalid Old Password")
    void testChangePassword_InvalidOldPassword() {
        // Arrange
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setOldPassword("wrongPassword");
        changePasswordDTO.setNewPassword("newPassword123");
        changePasswordDTO.setConfirmPassword("newPassword123");

        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String result = profileController.changePassword(changePasswordDTO, bindingResult, session, model);

        // Assert
        assertEquals("change-password", result);
        verify(model).addAttribute(eq("error"), anyString());
        verify(accountService, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("UTCID06: Change with Mismatched Passwords")
    void testChangePassword_MismatchedPasswords() {
        // Arrange
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setOldPassword("oldPassword123");
        changePasswordDTO.setNewPassword("newPassword123");
        changePasswordDTO.setConfirmPassword("differentPassword");

        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String result = profileController.changePassword(changePasswordDTO, bindingResult, session, model);

        // Assert
        assertEquals("change-password", result);
        verify(model).addAttribute(eq("error"), anyString());
        verify(accountService, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("UTCID07: Change with Same Old and New Password")
    void testChangePassword_SameOldAndNewPassword() {
        // Arrange
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setOldPassword("oldPassword123");
        changePasswordDTO.setNewPassword("oldPassword123");
        changePasswordDTO.setConfirmPassword("oldPassword123");

        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String result = profileController.changePassword(changePasswordDTO, bindingResult, session, model);

        // Assert
        assertEquals("change-password", result);
        verify(model).addAttribute(eq("error"), anyString());
        verify(accountService, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("UTCID08: Change with Binding Errors")
    void testChangePassword_BindingErrors() {
        // Arrange
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setOldPassword("oldPassword123");
        changePasswordDTO.setNewPassword("newPassword123");
        changePasswordDTO.setConfirmPassword("newPassword123");

        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String result = profileController.changePassword(changePasswordDTO, bindingResult, session, model);

        // Assert
        assertEquals("change-password", result);
        verify(accountService, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("UTCID09: Change with Empty Old Password")
    void testChangePassword_EmptyOldPassword() {
        // Arrange
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setOldPassword("");
        changePasswordDTO.setNewPassword("newPassword123");
        changePasswordDTO.setConfirmPassword("newPassword123");

        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String result = profileController.changePassword(changePasswordDTO, bindingResult, session, model);

        // Assert
        assertEquals("change-password", result);
        verify(model).addAttribute(eq("error"), anyString());
        verify(accountService, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("UTCID10: Change with Empty New Password")
    void testChangePassword_EmptyNewPassword() {
        // Arrange
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setOldPassword("oldPassword123");
        changePasswordDTO.setNewPassword("");
        changePasswordDTO.setConfirmPassword("newPassword123");

        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String result = profileController.changePassword(changePasswordDTO, bindingResult, session, model);

        // Assert
        assertEquals("change-password", result);
        verify(model).addAttribute(eq("error"), anyString());
        verify(accountService, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("UTCID11: Change with Empty Confirm Password")
    void testChangePassword_EmptyConfirmPassword() {
        // Arrange
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setOldPassword("oldPassword123");
        changePasswordDTO.setNewPassword("newPassword123");
        changePasswordDTO.setConfirmPassword("");

        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String result = profileController.changePassword(changePasswordDTO, bindingResult, session, model);

        // Assert
        assertEquals("change-password", result);
        verify(model).addAttribute(eq("error"), anyString());
        verify(accountService, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("UTCID12: Change with Short New Password")
    void testChangePassword_ShortNewPassword() {
        // Arrange
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setOldPassword("oldPassword123");
        changePasswordDTO.setNewPassword("123");
        changePasswordDTO.setConfirmPassword("123");

        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String result = profileController.changePassword(changePasswordDTO, bindingResult, session, model);

        // Assert
        assertEquals("change-password", result);
        verify(account).setPassword(anyString());
        verify(accountService).save(account);
        verify(model).addAttribute(eq("success"), eq("Đổi mật khẩu thành công!"));
    }

    @Test
    @DisplayName("UTCID13: Change with Long New Password")
    void testChangePassword_LongNewPassword() {
        // Arrange
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setOldPassword("oldPassword123");
        changePasswordDTO.setNewPassword("veryLongPassword123456789012345678901234567890");
        changePasswordDTO.setConfirmPassword("veryLongPassword123456789012345678901234567890");

        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String result = profileController.changePassword(changePasswordDTO, bindingResult, session, model);

        // Assert
        assertEquals("change-password", result);
        verify(account).setPassword(anyString());
        verify(accountService).save(account);
        verify(model).addAttribute(eq("success"), eq("Đổi mật khẩu thành công!"));
    }

    @Test
    @DisplayName("UTCID14: Change with Special Characters in New Password")
    void testChangePassword_SpecialCharactersInNewPassword() {
        // Arrange
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setOldPassword("oldPassword123");
        changePasswordDTO.setNewPassword("newPass@#$%^&*()");
        changePasswordDTO.setConfirmPassword("newPass@#$%^&*()");

        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String result = profileController.changePassword(changePasswordDTO, bindingResult, session, model);

        // Assert
        assertEquals("change-password", result);
        verify(account).setPassword(anyString());
        verify(accountService).save(account);
        verify(model).addAttribute(eq("success"), eq("Đổi mật khẩu thành công!"));
    }
}
