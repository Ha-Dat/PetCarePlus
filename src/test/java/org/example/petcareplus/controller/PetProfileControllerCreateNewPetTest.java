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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PetProfileControllerCreateNewPetTest {

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

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(petProfileController).build();
        
        // Setup default account and profile
        when(session.getAttribute("loggedInUser")).thenReturn(account);
        when(account.getProfile()).thenReturn(profile);
    }

    // ========== CREATENEWPET() METHOD TEST CASES ==========

    @Test
    @DisplayName("UTCID01: Cannot connect to server")
    void testCreateNewPet_ServerConnectionException() {
        // Arrange
        String name = "Luna";
        Integer age = 3;
        String species = "Cat";
        String breeds = "Persian";
        Float weight = 1.0f;
        MultipartFile imageFile = new MockMultipartFile("imageFile", "cat.png", "image/png", "test".getBytes());

        when(petProfileService.save(any(PetProfile.class)))
                .thenThrow(new RuntimeException("Server unavailable"));
        when(petProfileService.findByAccount(account)).thenReturn(new ArrayList<>());

        // Act
        String result = petProfileController.createNewPet(name, age, species, breeds, weight, imageFile, session, model);

        // Assert
        assertEquals("pet-profile", result);
        verify(model).addAttribute(eq("error"), eq("Có lỗi xảy ra khi tạo thú cưng: Server unavailable"));
        verify(petProfileService).findByAccount(account);
        verify(model).addAttribute(eq("petProfiles"), any());
        verify(model).addAttribute(eq("selectedPet"), eq(null));
        verify(model).addAttribute(eq("edit"), eq(false));
    }

    @Test
    @DisplayName("UTCID02: Register with NULL Name")
    void testCreateNewPet_NullName() {
        // Arrange
        String name = null;
        Integer age = 3;
        String species = "Cat";
        String breeds = "Persian";
        Float weight = 1.0f;
        MultipartFile imageFile = new MockMultipartFile("imageFile", "cat.png", "image/png", "test".getBytes());

        when(petProfileService.findByAccount(account)).thenReturn(new ArrayList<>());

        // Act
        String result = petProfileController.createNewPet(name, age, species, breeds, weight, imageFile, session, model);

        // Assert
        assertEquals("pet-profile", result);
        verify(model).addAttribute(eq("error"), eq("Có lỗi xảy ra khi tạo thú cưng: Cannot invoke \"String.trim()\" because \"name\" is null"));
        verify(petProfileService).findByAccount(account);
        verify(model).addAttribute(eq("petProfiles"), any());
        verify(model).addAttribute(eq("selectedPet"), eq(null));
        verify(model).addAttribute(eq("edit"), eq(false));
    }

    @Test
    @DisplayName("UTCID03: Successful Pet Creation")
    void testCreateNewPet_SuccessfulCreation() {
        // Arrange
        String name = "Luna";
        Integer age = 3;
        String species = "Cat";
        String breeds = "Persian";
        Float weight = 1.0f;
        MultipartFile imageFile = new MockMultipartFile("imageFile", "cat.png", "image/png", "test".getBytes());

        PetProfile savedPet = new PetProfile();
        savedPet.setPetProfileId(1L);
        savedPet.setName(name);
        savedPet.setAge(age);
        savedPet.setSpecies(species);
        savedPet.setBreeds(breeds);
        savedPet.setWeight(weight);

        when(petProfileService.save(any(PetProfile.class))).thenReturn(savedPet);

        // Act
        String result = petProfileController.createNewPet(name, age, species, breeds, weight, imageFile, session, model);

        // Assert
        assertEquals("redirect:/pet-profile?selectedId=1", result);
        verify(account).getProfile();
        verify(petProfileService).save(any(PetProfile.class));
        verify(petProfileService).uploadPetImage(eq(1L), eq(imageFile));
    }

    @Test
    @DisplayName("UTCID04: Register with NULL Age")
    void testCreateNewPet_NullAge() {
        // Arrange
        String name = "Luna";
        Integer age = null;
        String species = "Cat";
        String breeds = "Persian";
        Float weight = 1.0f;
        MultipartFile imageFile = new MockMultipartFile("imageFile", "cat.png", "image/png", "test".getBytes());

        when(petProfileService.save(any(PetProfile.class)))
                .thenThrow(new RuntimeException("Tuổi không được để trống"));
        when(petProfileService.findByAccount(account)).thenReturn(new ArrayList<>());

        // Act
        String result = petProfileController.createNewPet(name, age, species, breeds, weight, imageFile, session, model);

        // Assert
        assertEquals("pet-profile", result);
        verify(model).addAttribute(eq("error"), eq("Có lỗi xảy ra khi tạo thú cưng: Tuổi không được để trống"));
        verify(petProfileService).findByAccount(account);
        verify(model).addAttribute(eq("petProfiles"), any());
        verify(model).addAttribute(eq("selectedPet"), eq(null));
        verify(model).addAttribute(eq("edit"), eq(false));
    }

    @Test
    @DisplayName("UTCID05: Register with Age -1")
    void testCreateNewPet_AgeNegativeOne() {
        // Arrange
        String name = "Luna";
        Integer age = -1;
        String species = "Cat";
        String breeds = "Persian";
        Float weight = 1.0f;
        MultipartFile imageFile = new MockMultipartFile("imageFile", "cat.png", "image/png", "test".getBytes());

        when(petProfileService.save(any(PetProfile.class)))
                .thenThrow(new RuntimeException("Giá trị phải lớn hơn hoặc bằng 1"));
        when(petProfileService.findByAccount(account)).thenReturn(new ArrayList<>());

        // Act
        String result = petProfileController.createNewPet(name, age, species, breeds, weight, imageFile, session, model);

        // Assert
        assertEquals("pet-profile", result);
        verify(model).addAttribute(eq("error"), eq("Có lỗi xảy ra khi tạo thú cưng: Giá trị phải lớn hơn hoặc bằng 1"));
        verify(petProfileService).findByAccount(account);
        verify(model).addAttribute(eq("petProfiles"), any());
        verify(model).addAttribute(eq("selectedPet"), eq(null));
        verify(model).addAttribute(eq("edit"), eq(false));
    }

    @Test
    @DisplayName("UTCID06: Register with Age 301")
    void testCreateNewPet_AgeThreeHundredOne() {
        // Arrange
        String name = "Luna";
        Integer age = 301;
        String species = "Cat";
        String breeds = "Persian";
        Float weight = 1.0f;
        MultipartFile imageFile = new MockMultipartFile("imageFile", "cat.png", "image/png", "test".getBytes());

        when(petProfileService.save(any(PetProfile.class)))
                .thenThrow(new RuntimeException("Giá trị phải nhỏ hơn hoặc bằng 300"));
        when(petProfileService.findByAccount(account)).thenReturn(new ArrayList<>());

        // Act
        String result = petProfileController.createNewPet(name, age, species, breeds, weight, imageFile, session, model);

        // Assert
        assertEquals("pet-profile", result);
        verify(model).addAttribute(eq("error"), eq("Có lỗi xảy ra khi tạo thú cưng: Giá trị phải nhỏ hơn hoặc bằng 300"));
        verify(petProfileService).findByAccount(account);
        verify(model).addAttribute(eq("petProfiles"), any());
        verify(model).addAttribute(eq("selectedPet"), eq(null));
        verify(model).addAttribute(eq("edit"), eq(false));
    }

    @Test
    @DisplayName("UTCID07: Register with NULL Species")
    void testCreateNewPet_NullSpecies() {
        // Arrange
        String name = "Luna";
        Integer age = 3;
        String species = null;
        String breeds = "Persian";
        Float weight = 1.0f;
        MultipartFile imageFile = new MockMultipartFile("imageFile", "cat.png", "image/png", "test".getBytes());

        when(petProfileService.findByAccount(account)).thenReturn(new ArrayList<>());

        // Act
        String result = petProfileController.createNewPet(name, age, species, breeds, weight, imageFile, session, model);

        // Assert
        assertEquals("pet-profile", result);
        verify(model).addAttribute(eq("error"), eq("Có lỗi xảy ra khi tạo thú cưng: Cannot invoke \"String.trim()\" because \"species\" is null"));
        verify(petProfileService).findByAccount(account);
        verify(model).addAttribute(eq("petProfiles"), any());
        verify(model).addAttribute(eq("selectedPet"), eq(null));
        verify(model).addAttribute(eq("edit"), eq(false));
    }

    @Test
    @DisplayName("UTCID08: Register with NULL Breeds")
    void testCreateNewPet_NullBreeds() {
        // Arrange
        String name = "Luna";
        Integer age = 3;
        String species = "Cat";
        String breeds = null;
        Float weight = 1.0f;
        MultipartFile imageFile = new MockMultipartFile("imageFile", "cat.png", "image/png", "test".getBytes());

        when(petProfileService.findByAccount(account)).thenReturn(new ArrayList<>());

        // Act
        String result = petProfileController.createNewPet(name, age, species, breeds, weight, imageFile, session, model);

        // Assert
        assertEquals("pet-profile", result);
        verify(model).addAttribute(eq("error"), eq("Có lỗi xảy ra khi tạo thú cưng: Cannot invoke \"String.trim()\" because \"breeds\" is null"));
        verify(petProfileService).findByAccount(account);
        verify(model).addAttribute(eq("petProfiles"), any());
        verify(model).addAttribute(eq("selectedPet"), eq(null));
        verify(model).addAttribute(eq("edit"), eq(false));
    }

    @Test
    @DisplayName("UTCID09: Register with NULL Weight")
    void testCreateNewPet_NullWeight() {
        // Arrange
        String name = "Luna";
        Integer age = 3;
        String species = "Cat";
        String breeds = "Persian";
        Float weight = null;
        MultipartFile imageFile = new MockMultipartFile("imageFile", "cat.png", "image/png", "test".getBytes());

        when(petProfileService.save(any(PetProfile.class)))
                .thenThrow(new RuntimeException("Cân nặng không được để trống"));
        when(petProfileService.findByAccount(account)).thenReturn(new ArrayList<>());

        // Act
        String result = petProfileController.createNewPet(name, age, species, breeds, weight, imageFile, session, model);

        // Assert
        assertEquals("pet-profile", result);
        verify(model).addAttribute(eq("error"), eq("Có lỗi xảy ra khi tạo thú cưng: Cân nặng không được để trống"));
        verify(petProfileService).findByAccount(account);
        verify(model).addAttribute(eq("petProfiles"), any());
        verify(model).addAttribute(eq("selectedPet"), eq(null));
        verify(model).addAttribute(eq("edit"), eq(false));
    }

    @Test
    @DisplayName("UTCID10: Register with Invalid Species Format")
    void testCreateNewPet_InvalidSpeciesFormat() {
        // Arrange
        String name = "Luna";
        Integer age = 3;
        String species = "Cat123"; // Invalid format with numbers
        String breeds = "Persian";
        Float weight = 1.0f;
        MultipartFile imageFile = new MockMultipartFile("imageFile", "cat.png", "image/png", "test".getBytes());

        when(petProfileService.save(any(PetProfile.class)))
                .thenThrow(new RuntimeException("Vui Lòng đúng với định dạng yêu cầu"));
        when(petProfileService.findByAccount(account)).thenReturn(new ArrayList<>());

        // Act
        String result = petProfileController.createNewPet(name, age, species, breeds, weight, imageFile, session, model);

        // Assert
        assertEquals("pet-profile", result);
        verify(model).addAttribute(eq("error"), eq("Có lỗi xảy ra khi tạo thú cưng: Vui Lòng đúng với định dạng yêu cầu"));
        verify(petProfileService).findByAccount(account);
        verify(model).addAttribute(eq("petProfiles"), any());
        verify(model).addAttribute(eq("selectedPet"), eq(null));
        verify(model).addAttribute(eq("edit"), eq(false));
    }

    @Test
    @DisplayName("UTCID11: Server unavailable during save")
    void testCreateNewPet_ServerUnavailableDuringSave() {
        // Arrange
        String name = "Luna";
        Integer age = 3;
        String species = "Cat";
        String breeds = "Persian";
        Float weight = 1.0f;
        MultipartFile imageFile = new MockMultipartFile("imageFile", "cat.png", "image/png", "test".getBytes());

        when(petProfileService.save(any(PetProfile.class)))
                .thenThrow(new RuntimeException("Server unavailable"));
        when(petProfileService.findByAccount(account)).thenReturn(new ArrayList<>());

        // Act
        String result = petProfileController.createNewPet(name, age, species, breeds, weight, imageFile, session, model);

        // Assert
        assertEquals("pet-profile", result);
        verify(model).addAttribute(eq("error"), eq("Có lỗi xảy ra khi tạo thú cưng: Server unavailable"));
        verify(petProfileService).findByAccount(account);
        verify(model).addAttribute(eq("petProfiles"), any());
        verify(model).addAttribute(eq("selectedPet"), eq(null));
        verify(model).addAttribute(eq("edit"), eq(false));
    }

    @Test
    @DisplayName("UTCID12: Register with Invalid Breeds Format")
    void testCreateNewPet_InvalidBreedsFormat() {
        // Arrange
        String name = "Luna";
        Integer age = 3;
        String species = "Cat";
        String breeds = "Persian123"; // Invalid format with numbers
        Float weight = 1.0f;
        MultipartFile imageFile = new MockMultipartFile("imageFile", "cat.png", "image/png", "test".getBytes());

        when(petProfileService.save(any(PetProfile.class)))
                .thenThrow(new RuntimeException("Vui Lòng đúng với định dạng yêu cầu"));
        when(petProfileService.findByAccount(account)).thenReturn(new ArrayList<>());

        // Act
        String result = petProfileController.createNewPet(name, age, species, breeds, weight, imageFile, session, model);

        // Assert
        assertEquals("pet-profile", result);
        verify(model).addAttribute(eq("error"), eq("Có lỗi xảy ra khi tạo thú cưng: Vui Lòng đúng với định dạng yêu cầu"));
        verify(petProfileService).findByAccount(account);
        verify(model).addAttribute(eq("petProfiles"), any());
        verify(model).addAttribute(eq("selectedPet"), eq(null));
        verify(model).addAttribute(eq("edit"), eq(false));
    }

    @Test
    @DisplayName("UTCID13: Register with Weight -1")
    void testCreateNewPet_WeightNegativeOne() {
        // Arrange
        String name = "Luna";
        Integer age = 3;
        String species = "Cat";
        String breeds = "Persian";
        Float weight = -1.0f;
        MultipartFile imageFile = new MockMultipartFile("imageFile", "cat.png", "image/png", "test".getBytes());

        when(petProfileService.save(any(PetProfile.class)))
                .thenThrow(new RuntimeException("Giá trị phải lớn hơn hoặc bằng 1"));
        when(petProfileService.findByAccount(account)).thenReturn(new ArrayList<>());

        // Act
        String result = petProfileController.createNewPet(name, age, species, breeds, weight, imageFile, session, model);

        // Assert
        assertEquals("pet-profile", result);
        verify(model).addAttribute(eq("error"), eq("Có lỗi xảy ra khi tạo thú cưng: Giá trị phải lớn hơn hoặc bằng 1"));
        verify(petProfileService).findByAccount(account);
        verify(model).addAttribute(eq("petProfiles"), any());
        verify(model).addAttribute(eq("selectedPet"), eq(null));
        verify(model).addAttribute(eq("edit"), eq(false));
    }

    @Test
    @DisplayName("UTCID14: Register with Weight 101")
    void testCreateNewPet_WeightOneHundredOne() {
        // Arrange
        String name = "Luna";
        Integer age = 3;
        String species = "Cat";
        String breeds = "Persian";
        Float weight = 101.0f;
        MultipartFile imageFile = new MockMultipartFile("imageFile", "cat.png", "image/png", "test".getBytes());

        when(petProfileService.save(any(PetProfile.class)))
                .thenThrow(new RuntimeException("Giá trị phải nhỏ hơn hoặc bằng 100"));
        when(petProfileService.findByAccount(account)).thenReturn(new ArrayList<>());

        // Act
        String result = petProfileController.createNewPet(name, age, species, breeds, weight, imageFile, session, model);

        // Assert
        assertEquals("pet-profile", result);
        verify(model).addAttribute(eq("error"), eq("Có lỗi xảy ra khi tạo thú cưng: Giá trị phải nhỏ hơn hoặc bằng 100"));
        verify(petProfileService).findByAccount(account);
        verify(model).addAttribute(eq("petProfiles"), any());
        verify(model).addAttribute(eq("selectedPet"), eq(null));
        verify(model).addAttribute(eq("edit"), eq(false));
    }
}
