package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.PetProfile;
import org.example.petcareplus.entity.Profile;
import org.example.petcareplus.service.PetProfileService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PetProfileControllerEditPetProfileTest {

    @Mock
    private PetProfileService petProfileService;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @Mock
    private Account account;

    @Mock
    private Profile profile;

    @InjectMocks
    private PetProfileController petProfileController;

    @BeforeEach
    void setUp() {
        // Setup default account and profile
        when(session.getAttribute("loggedInUser")).thenReturn(account);
        when(account.getProfile()).thenReturn(profile);
    }

    // ========== EDITPETPROFILE() METHOD TEST CASES ==========

    @Test
    @DisplayName("UTCID01: Edit with NULL Name")
    void testEditPetProfile_NullName() {
        // Arrange
        Long petId = 1L;
        String name = null;
        String species = "Cat";
        String breeds = "Persian";
        Integer age = 3;
        Float weight = 4.0f;

        PetProfile existingPet = new PetProfile();
        existingPet.setPetProfileId(petId);
        when(petProfileService.findById(petId)).thenReturn(existingPet);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            petProfileController.editPetProfile(petId, name, species, breeds, age, weight, session, model);
        });
        
        verify(petProfileService).findById(petId);
        verify(petProfileService, never()).updatePetProfile(anyLong(), any(PetProfile.class));
    }

    @Test
    @DisplayName("UTCID02: Edit with NULL Age")
    void testEditPetProfile_NullAge() {
        // Arrange
        Long petId = 1L;
        String name = "Luna";
        String species = "Cat";
        String breeds = "Persian";
        Integer age = null;
        Float weight = 4.0f;

        PetProfile existingPet = new PetProfile();
        existingPet.setPetProfileId(petId);
        when(petProfileService.findById(petId)).thenReturn(existingPet);

        // Act
        String result = petProfileController.editPetProfile(petId, name, species, breeds, age, weight, session, model);

        // Assert
        assertEquals("redirect:/pet-profile?selectedId=1", result);
        verify(petProfileService).findById(petId);
        verify(petProfileService).updatePetProfile(anyLong(), any(PetProfile.class));
    }

    @Test
    @DisplayName("UTCID03: Successful Pet Edit")
    void testEditPetProfile_SuccessfulEdit() {
        // Arrange
        Long petId = 1L;
        String name = "Luna";
        String species = "Cat";
        String breeds = "Persian";
        Integer age = 3;
        Float weight = 4.0f;

        PetProfile existingPet = new PetProfile();
        existingPet.setPetProfileId(petId);
        when(petProfileService.findById(petId)).thenReturn(existingPet);

        // Act
        String result = petProfileController.editPetProfile(petId, name, species, breeds, age, weight, session, model);

        // Assert
        assertEquals("redirect:/pet-profile?selectedId=1", result);
        verify(petProfileService).findById(petId);
        verify(petProfileService).updatePetProfile(anyLong(), any(PetProfile.class));
    }

    @Test
    @DisplayName("UTCID04: Edit with Age 301")
    void testEditPetProfile_AgeThreeHundredOne() {
        // Arrange
        Long petId = 1L;
        String name = "Luna";
        String species = "Cat";
        String breeds = "Persian";
        Integer age = 301;
        Float weight = 4.0f;

        PetProfile existingPet = new PetProfile();
        existingPet.setPetProfileId(petId);
        when(petProfileService.findById(petId)).thenReturn(existingPet);

        // Act
        String result = petProfileController.editPetProfile(petId, name, species, breeds, age, weight, session, model);

        // Assert
        assertEquals("redirect:/pet-profile?selectedId=1", result);
        verify(petProfileService).findById(petId);
        verify(petProfileService).updatePetProfile(anyLong(), any(PetProfile.class));
    }

    @Test
    @DisplayName("UTCID05: Edit with Age -1")
    void testEditPetProfile_AgeNegativeOne() {
        // Arrange
        Long petId = 1L;
        String name = "Luna";
        String species = "Cat";
        String breeds = "Persian";
        Integer age = -1;
        Float weight = 4.0f;

        PetProfile existingPet = new PetProfile();
        existingPet.setPetProfileId(petId);
        when(petProfileService.findById(petId)).thenReturn(existingPet);

        // Act
        String result = petProfileController.editPetProfile(petId, name, species, breeds, age, weight, session, model);

        // Assert
        assertEquals("redirect:/pet-profile?selectedId=1", result);
        verify(petProfileService).findById(petId);
        verify(petProfileService).updatePetProfile(anyLong(), any(PetProfile.class));
    }

    @Test
    @DisplayName("UTCID06: Edit with NULL Species")
    void testEditPetProfile_NullSpecies() {
        // Arrange
        Long petId = 1L;
        String name = "Luna";
        String species = null;
        String breeds = "Persian";
        Integer age = 3;
        Float weight = 4.0f;

        PetProfile existingPet = new PetProfile();
        existingPet.setPetProfileId(petId);
        when(petProfileService.findById(petId)).thenReturn(existingPet);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            petProfileController.editPetProfile(petId, name, species, breeds, age, weight, session, model);
        });
        
        verify(petProfileService).findById(petId);
        verify(petProfileService, never()).updatePetProfile(anyLong(), any(PetProfile.class));
    }

    @Test
    @DisplayName("UTCID07: Edit with NULL Breeds")
    void testEditPetProfile_NullBreeds() {
        // Arrange
        Long petId = 1L;
        String name = "Luna";
        String species = "Cat";
        String breeds = null;
        Integer age = 3;
        Float weight = 4.0f;

        PetProfile existingPet = new PetProfile();
        existingPet.setPetProfileId(petId);
        when(petProfileService.findById(petId)).thenReturn(existingPet);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            petProfileController.editPetProfile(petId, name, species, breeds, age, weight, session, model);
        });
        
        verify(petProfileService).findById(petId);
        verify(petProfileService, never()).updatePetProfile(anyLong(), any(PetProfile.class));
    }

    @Test
    @DisplayName("UTCID08: Edit with NULL Weight")
    void testEditPetProfile_NullWeight() {
        // Arrange
        Long petId = 1L;
        String name = "Luna";
        String species = "Cat";
        String breeds = "Persian";
        Integer age = 3;
        Float weight = null;

        PetProfile existingPet = new PetProfile();
        existingPet.setPetProfileId(petId);
        when(petProfileService.findById(petId)).thenReturn(existingPet);

        // Act
        String result = petProfileController.editPetProfile(petId, name, species, breeds, age, weight, session, model);

        // Assert
        assertEquals("redirect:/pet-profile?selectedId=1", result);
        verify(petProfileService).findById(petId);
        verify(petProfileService).updatePetProfile(anyLong(), any(PetProfile.class));
    }

    @Test
    @DisplayName("UTCID09: Edit with Invalid Format Values")
    void testEditPetProfile_InvalidFormatValues() {
        // Arrange
        Long petId = 1L;
        String name = "Abc123";
        String species = "Cat123";
        String breeds = "Persian123";
        Integer age = 3;
        Float weight = 4.0f;

        PetProfile existingPet = new PetProfile();
        existingPet.setPetProfileId(petId);
        when(petProfileService.findById(petId)).thenReturn(existingPet);

        // Act
        String result = petProfileController.editPetProfile(petId, name, species, breeds, age, weight, session, model);

        // Assert
        assertEquals("redirect:/pet-profile?selectedId=1", result);
        verify(petProfileService).findById(petId);
        verify(petProfileService).updatePetProfile(anyLong(), any(PetProfile.class));
    }

    @Test
    @DisplayName("UTCID10: Edit with Weight 101")
    void testEditPetProfile_WeightOneHundredOne() {
        // Arrange
        Long petId = 1L;
        String name = "Luna";
        String species = "Cat";
        String breeds = "Persian";
        Integer age = 3;
        Float weight = 101.0f;

        PetProfile existingPet = new PetProfile();
        existingPet.setPetProfileId(petId);
        when(petProfileService.findById(petId)).thenReturn(existingPet);

        // Act
        String result = petProfileController.editPetProfile(petId, name, species, breeds, age, weight, session, model);

        // Assert
        assertEquals("redirect:/pet-profile?selectedId=1", result);
        verify(petProfileService).findById(petId);
        verify(petProfileService).updatePetProfile(anyLong(), any(PetProfile.class));
    }

    @Test
    @DisplayName("UTCID11: Edit with Weight -1")
    void testEditPetProfile_WeightNegativeOne() {
        // Arrange
        Long petId = 1L;
        String name = "Luna";
        String species = "Cat";
        String breeds = "Persian";
        Integer age = 3;
        Float weight = -1.0f;

        PetProfile existingPet = new PetProfile();
        existingPet.setPetProfileId(petId);
        when(petProfileService.findById(petId)).thenReturn(existingPet);

        // Act
        String result = petProfileController.editPetProfile(petId, name, species, breeds, age, weight, session, model);

        // Assert
        assertEquals("redirect:/pet-profile?selectedId=1", result);
        verify(petProfileService).findById(petId);
        verify(petProfileService).updatePetProfile(anyLong(), any(PetProfile.class));
    }

    @Test
    @DisplayName("UTCID12: Server unavailable during update")
    void testEditPetProfile_ServerUnavailable() {
        // Arrange
        Long petId = 1L;
        String name = "Luna";
        String species = "Cat";
        String breeds = "Persian";
        Integer age = 3;
        Float weight = 4.0f;

        PetProfile existingPet = new PetProfile();
        existingPet.setPetProfileId(petId);
        when(petProfileService.findById(petId)).thenReturn(existingPet);
        doThrow(new RuntimeException("Server unavailable"))
                .when(petProfileService).updatePetProfile(anyLong(), any(PetProfile.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            petProfileController.editPetProfile(petId, name, species, breeds, age, weight, session, model);
        });
        
        verify(petProfileService).findById(petId);
        verify(petProfileService).updatePetProfile(anyLong(), any(PetProfile.class));
    }

    @Test
    @DisplayName("UTCID13: Edit with Invalid Format Validation")
    void testEditPetProfile_InvalidFormatValidation() {
        // Arrange
        Long petId = 1L;
        String name = "Luna";
        String species = "Cat";
        String breeds = "Persian";
        Integer age = 3;
        Float weight = 4.0f;

        PetProfile existingPet = new PetProfile();
        existingPet.setPetProfileId(petId);
        when(petProfileService.findById(petId)).thenReturn(existingPet);
        doThrow(new RuntimeException("Vui Lòng đúng với định dạng yêu cầu"))
                .when(petProfileService).updatePetProfile(anyLong(), any(PetProfile.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            petProfileController.editPetProfile(petId, name, species, breeds, age, weight, session, model);
        });
        
        verify(petProfileService).findById(petId);
        verify(petProfileService).updatePetProfile(anyLong(), any(PetProfile.class));
    }

    @Test
    @DisplayName("UTCID14: Edit with Format Validation Error")
    void testEditPetProfile_FormatValidationError() {
        // Arrange
        Long petId = 1L;
        String name = "Luna";
        String species = "Cat";
        String breeds = "Persian";
        Integer age = 3;
        Float weight = 4.0f;

        PetProfile existingPet = new PetProfile();
        existingPet.setPetProfileId(petId);
        when(petProfileService.findById(petId)).thenReturn(existingPet);
        doThrow(new RuntimeException("Vui Lòng đúng với định dạng yêu cầu"))
                .when(petProfileService).updatePetProfile(anyLong(), any(PetProfile.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            petProfileController.editPetProfile(petId, name, species, breeds, age, weight, session, model);
        });
        
        verify(petProfileService).findById(petId);
        verify(petProfileService).updatePetProfile(anyLong(), any(PetProfile.class));
    }
}
