package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.entity.*;
import org.example.petcareplus.enums.AccountRole;
import org.example.petcareplus.enums.AccountStatus;
import org.example.petcareplus.enums.BookingStatus;
import org.example.petcareplus.enums.ServiceCategory;
import org.example.petcareplus.enums.ServiceStatus;
import org.example.petcareplus.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SpaBookingControllerAddSpaBookingTest {

    @Mock
    private SpaBookingService spaBookingService;

    @Mock
    private PetProfileService petProfileService;

    @Mock
    private ServiceService serviceService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private Model model;

    @InjectMocks
    private SpaBookingController spaBookingController;

    private MockHttpSession session;
    private Account testAccount;
    private Profile testProfile;
    private PetProfile testPetProfile;
    private Service testService;
    private SpaBooking testSpaBooking;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        
        // Setup test account
        testAccount = new Account();
        testAccount.setAccountId(1L);
        testAccount.setName("Nguyen Van A");
        testAccount.setPhone("0912345678");
        testAccount.setRole(AccountRole.CUSTOMER);
        testAccount.setStatus(AccountStatus.ACTIVE);
        
        // Setup test profile
        testProfile = new Profile();
        testProfile.setProfileId(1L);
        testAccount.setProfile(testProfile);
        
        // Setup test pet profile
        testPetProfile = new PetProfile();
        testPetProfile.setPetProfileId(1L);
        testPetProfile.setName("Luna");
        testPetProfile.setSpecies("Cat");
        testPetProfile.setBreeds("Persian");
        testPetProfile.setWeight(10.0f);
        testPetProfile.setAge(5);
        testPetProfile.setProfile(testProfile);
        
        // Setup test service
        testService = new Service();
        testService.setServiceId(1L);
        testService.setName("Spa Treatment");
        testService.setServiceCategory(ServiceCategory.SPA);
        testService.setStatus(ServiceStatus.ACTIVE);
        testService.setPrice(new BigDecimal("100000"));
        testService.setDuration(60);
        
        // Setup test spa booking
        testSpaBooking = new SpaBooking();
        testSpaBooking.setSpaBookingId(1L);
        testSpaBooking.setBookDate(LocalDateTime.now().plusHours(2));
        testSpaBooking.setNote("Test note");
        testSpaBooking.setStatus(BookingStatus.PENDING);
        testSpaBooking.setPetProfile(testPetProfile);
        testSpaBooking.setService(testService);
        
        // Setup session
        session.setAttribute("loggedInUser", testAccount);
    }

    // ========== ADDSPABOOKING() METHOD TEST CASES ==========

    @Test
    @DisplayName("UTCID00: Add Spa Booking Successfully")
    void testAddSpaBooking_Success() {
        // Arrange
        LocalDateTime bookDate = LocalDateTime.now().plusHours(2);
        String note = "Test note";
        Long serviceId = 1L;
        String petName = "Luna";
        String petSpecies = "Cat";
        String petBreed = "Persian";
        float petWeight = 10.0f;
        Integer petAge = 5;
        Long petProfileId = 1L;

        when(petProfileService.findById(petProfileId)).thenReturn(testPetProfile);
        when(spaBookingService.Service_findById(serviceId)).thenReturn(Optional.of(testService));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(testSpaBooking);

        // Act
        String result = spaBookingController.addSpaBooking(
                bookDate, note, serviceId, petName, petSpecies, petBreed, 
                petWeight, petAge, petProfileId, session, model
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(petProfileService).updatePetProfile(petProfileId, testPetProfile);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID01: Add Spa Booking with Empty Pet Name")
    void testAddSpaBooking_EmptyPetName() {
        // Arrange
        LocalDateTime bookDate = LocalDateTime.now().plusHours(2);
        String note = "Test note";
        Long serviceId = 1L;
        String petName = "";
        String petSpecies = "Cat";
        String petBreed = "Persian";
        float petWeight = 10.0f;
        Integer petAge = 5;
        Long petProfileId = 1L;

        when(petProfileService.findById(petProfileId)).thenReturn(testPetProfile);
        when(spaBookingService.Service_findById(serviceId)).thenReturn(Optional.of(testService));

        // Act
        String result = spaBookingController.addSpaBooking(
                bookDate, note, serviceId, petName, petSpecies, petBreed, 
                petWeight, petAge, petProfileId, session, model
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(petProfileService).updatePetProfile(petProfileId, testPetProfile);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID02: Add Spa Booking with Valid Pet Name")
    void testAddSpaBooking_ValidPetName() {
        // Arrange
        LocalDateTime bookDate = LocalDateTime.now().plusHours(2);
        String note = "Test note";
        Long serviceId = 1L;
        String petName = "Luna";
        String petSpecies = "Cat";
        String petBreed = "Persian";
        float petWeight = 10.0f;
        Integer petAge = 5;
        Long petProfileId = 1L;

        when(petProfileService.findById(petProfileId)).thenReturn(testPetProfile);
        when(spaBookingService.Service_findById(serviceId)).thenReturn(Optional.of(testService));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(testSpaBooking);

        // Act
        String result = spaBookingController.addSpaBooking(
                bookDate, note, serviceId, petName, petSpecies, petBreed, 
                petWeight, petAge, petProfileId, session, model
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(petProfileService).updatePetProfile(petProfileId, testPetProfile);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID03: Add Spa Booking with NULL Pet Species")
    void testAddSpaBooking_NullPetSpecies() {
        // Arrange
        LocalDateTime bookDate = LocalDateTime.now().plusHours(2);
        String note = "Test note";
        Long serviceId = 1L;
        String petName = "Luna";
        String petSpecies = null;
        String petBreed = "Persian";
        float petWeight = 10.0f;
        Integer petAge = 5;
        Long petProfileId = 1L;

        when(petProfileService.findById(petProfileId)).thenReturn(testPetProfile);
        when(spaBookingService.Service_findById(serviceId)).thenReturn(Optional.of(testService));

        // Act
        String result = spaBookingController.addSpaBooking(
                bookDate, note, serviceId, petName, petSpecies, petBreed, 
                petWeight, petAge, petProfileId, session, model
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(petProfileService).updatePetProfile(petProfileId, testPetProfile);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID04: Add Spa Booking with Empty Pet Species")
    void testAddSpaBooking_EmptyPetSpecies() {
        // Arrange
        LocalDateTime bookDate = LocalDateTime.now().plusHours(2);
        String note = "Test note";
        Long serviceId = 1L;
        String petName = "Luna";
        String petSpecies = "";
        String petBreed = "Persian";
        float petWeight = 10.0f;
        Integer petAge = 5;
        Long petProfileId = 1L;

        when(petProfileService.findById(petProfileId)).thenReturn(testPetProfile);
        when(spaBookingService.Service_findById(serviceId)).thenReturn(Optional.of(testService));

        // Act
        String result = spaBookingController.addSpaBooking(
                bookDate, note, serviceId, petName, petSpecies, petBreed, 
                petWeight, petAge, petProfileId, session, model
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(petProfileService).updatePetProfile(petProfileId, testPetProfile);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID05: Add Spa Booking with Invalid Pet Species")
    void testAddSpaBooking_InvalidPetSpecies() {
        // Arrange
        LocalDateTime bookDate = LocalDateTime.now().plusHours(2);
        String note = "Test note";
        Long serviceId = 1L;
        String petName = "Luna";
        String petSpecies = "1234";
        String petBreed = "Persian";
        float petWeight = 10.0f;
        Integer petAge = 5;
        Long petProfileId = 1L;

        when(petProfileService.findById(petProfileId)).thenReturn(testPetProfile);
        when(spaBookingService.Service_findById(serviceId)).thenReturn(Optional.of(testService));

        // Act
        String result = spaBookingController.addSpaBooking(
                bookDate, note, serviceId, petName, petSpecies, petBreed, 
                petWeight, petAge, petProfileId, session, model
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(petProfileService).updatePetProfile(petProfileId, testPetProfile);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID06: Add Spa Booking with Valid Pet Species")
    void testAddSpaBooking_ValidPetSpecies() {
        // Arrange
        LocalDateTime bookDate = LocalDateTime.now().plusHours(2);
        String note = "Test note";
        Long serviceId = 1L;
        String petName = "Luna";
        String petSpecies = "Cat";
        String petBreed = "Persian";
        float petWeight = 10.0f;
        Integer petAge = 5;
        Long petProfileId = 1L;

        when(petProfileService.findById(petProfileId)).thenReturn(testPetProfile);
        when(spaBookingService.Service_findById(serviceId)).thenReturn(Optional.of(testService));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(testSpaBooking);

        // Act
        String result = spaBookingController.addSpaBooking(
                bookDate, note, serviceId, petName, petSpecies, petBreed, 
                petWeight, petAge, petProfileId, session, model
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(petProfileService).updatePetProfile(petProfileId, testPetProfile);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID07: Add Spa Booking with NULL Pet Breed")
    void testAddSpaBooking_NullPetBreed() {
        // Arrange
        LocalDateTime bookDate = LocalDateTime.now().plusHours(2);
        String note = "Test note";
        Long serviceId = 1L;
        String petName = "Luna";
        String petSpecies = "Cat";
        String petBreed = null;
        float petWeight = 10.0f;
        Integer petAge = 5;
        Long petProfileId = 1L;

        when(petProfileService.findById(petProfileId)).thenReturn(testPetProfile);
        when(spaBookingService.Service_findById(serviceId)).thenReturn(Optional.of(testService));

        // Act
        String result = spaBookingController.addSpaBooking(
                bookDate, note, serviceId, petName, petSpecies, petBreed, 
                petWeight, petAge, petProfileId, session, model
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(petProfileService).updatePetProfile(petProfileId, testPetProfile);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID08: Add Spa Booking with Empty Pet Breed")
    void testAddSpaBooking_EmptyPetBreed() {
        // Arrange
        LocalDateTime bookDate = LocalDateTime.now().plusHours(2);
        String note = "Test note";
        Long serviceId = 1L;
        String petName = "Luna";
        String petSpecies = "Cat";
        String petBreed = "";
        float petWeight = 10.0f;
        Integer petAge = 5;
        Long petProfileId = 1L;

        when(petProfileService.findById(petProfileId)).thenReturn(testPetProfile);
        when(spaBookingService.Service_findById(serviceId)).thenReturn(Optional.of(testService));

        // Act
        String result = spaBookingController.addSpaBooking(
                bookDate, note, serviceId, petName, petSpecies, petBreed, 
                petWeight, petAge, petProfileId, session, model
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(petProfileService).updatePetProfile(petProfileId, testPetProfile);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID09: Add Spa Booking with NULL Pet Weight")
    void testAddSpaBooking_NullPetWeight() {
        // Arrange
        LocalDateTime bookDate = LocalDateTime.now().plusHours(2);
        String note = "Test note";
        Long serviceId = 1L;
        String petName = "Luna";
        String petSpecies = "Cat";
        String petBreed = "Persian";
        float petWeight = 0.0f; // Simulating null weight
        Integer petAge = 5;
        Long petProfileId = 1L;

        when(petProfileService.findById(petProfileId)).thenReturn(testPetProfile);
        when(spaBookingService.Service_findById(serviceId)).thenReturn(Optional.of(testService));

        // Act
        String result = spaBookingController.addSpaBooking(
                bookDate, note, serviceId, petName, petSpecies, petBreed, 
                petWeight, petAge, petProfileId, session, model
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(petProfileService).updatePetProfile(petProfileId, testPetProfile);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID10: Add Spa Booking with Empty Pet Weight")
    void testAddSpaBooking_EmptyPetWeight() {
        // Arrange
        LocalDateTime bookDate = LocalDateTime.now().plusHours(2);
        String note = "Test note";
        Long serviceId = 1L;
        String petName = "Luna";
        String petSpecies = "Cat";
        String petBreed = "Persian";
        float petWeight = 0.0f; // Simulating empty weight
        Integer petAge = 5;
        Long petProfileId = 1L;

        when(petProfileService.findById(petProfileId)).thenReturn(testPetProfile);
        when(spaBookingService.Service_findById(serviceId)).thenReturn(Optional.of(testService));

        // Act
        String result = spaBookingController.addSpaBooking(
                bookDate, note, serviceId, petName, petSpecies, petBreed, 
                petWeight, petAge, petProfileId, session, model
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(petProfileService).updatePetProfile(petProfileId, testPetProfile);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID11: Add Spa Booking with Negative Pet Weight")
    void testAddSpaBooking_NegativePetWeight() {
        // Arrange
        LocalDateTime bookDate = LocalDateTime.now().plusHours(2);
        String note = "Test note";
        Long serviceId = 1L;
        String petName = "Luna";
        String petSpecies = "Cat";
        String petBreed = "Persian";
        float petWeight = -1.0f;
        Integer petAge = 5;
        Long petProfileId = 1L;

        when(petProfileService.findById(petProfileId)).thenReturn(testPetProfile);
        when(spaBookingService.Service_findById(serviceId)).thenReturn(Optional.of(testService));

        // Act
        String result = spaBookingController.addSpaBooking(
                bookDate, note, serviceId, petName, petSpecies, petBreed, 
                petWeight, petAge, petProfileId, session, model
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(petProfileService).updatePetProfile(petProfileId, testPetProfile);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID12: Add Spa Booking with Invalid Pet Weight")
    void testAddSpaBooking_InvalidPetWeight() {
        // Arrange
        LocalDateTime bookDate = LocalDateTime.now().plusHours(2);
        String note = "Test note";
        Long serviceId = 1L;
        String petName = "Luna";
        String petSpecies = "Cat";
        String petBreed = "Persian";
        float petWeight = 101.0f; // Invalid weight > 100
        Integer petAge = 5;
        Long petProfileId = 1L;

        when(petProfileService.findById(petProfileId)).thenReturn(testPetProfile);
        when(spaBookingService.Service_findById(serviceId)).thenReturn(Optional.of(testService));

        // Act
        String result = spaBookingController.addSpaBooking(
                bookDate, note, serviceId, petName, petSpecies, petBreed, 
                petWeight, petAge, petProfileId, session, model
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(petProfileService).updatePetProfile(petProfileId, testPetProfile);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID13: Add Spa Booking with Valid Pet Weight")
    void testAddSpaBooking_ValidPetWeight() {
        // Arrange
        LocalDateTime bookDate = LocalDateTime.now().plusHours(2);
        String note = "Test note";
        Long serviceId = 1L;
        String petName = "Luna";
        String petSpecies = "Cat";
        String petBreed = "Persian";
        float petWeight = 10.0f;
        Integer petAge = 5;
        Long petProfileId = 1L;

        when(petProfileService.findById(petProfileId)).thenReturn(testPetProfile);
        when(spaBookingService.Service_findById(serviceId)).thenReturn(Optional.of(testService));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(testSpaBooking);

        // Act
        String result = spaBookingController.addSpaBooking(
                bookDate, note, serviceId, petName, petSpecies, petBreed, 
                petWeight, petAge, petProfileId, session, model
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(petProfileService).updatePetProfile(petProfileId, testPetProfile);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID14: Add Spa Booking with NULL Pet Age")
    void testAddSpaBooking_NullPetAge() {
        // Arrange
        LocalDateTime bookDate = LocalDateTime.now().plusHours(2);
        String note = "Test note";
        Long serviceId = 1L;
        String petName = "Luna";
        String petSpecies = "Cat";
        String petBreed = "Persian";
        float petWeight = 10.0f;
        Integer petAge = null;
        Long petProfileId = 1L;

        when(petProfileService.findById(petProfileId)).thenReturn(testPetProfile);
        when(spaBookingService.Service_findById(serviceId)).thenReturn(Optional.of(testService));

        // Act
        String result = spaBookingController.addSpaBooking(
                bookDate, note, serviceId, petName, petSpecies, petBreed, 
                petWeight, petAge, petProfileId, session, model
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(petProfileService).updatePetProfile(petProfileId, testPetProfile);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID15: Add Spa Booking with Empty Pet Age")
    void testAddSpaBooking_EmptyPetAge() {
        // Arrange
        LocalDateTime bookDate = LocalDateTime.now().plusHours(2);
        String note = "Test note";
        Long serviceId = 1L;
        String petName = "Luna";
        String petSpecies = "Cat";
        String petBreed = "Persian";
        float petWeight = 10.0f;
        Integer petAge = 0; // Simulating empty age
        Long petProfileId = 1L;

        when(petProfileService.findById(petProfileId)).thenReturn(testPetProfile);
        when(spaBookingService.Service_findById(serviceId)).thenReturn(Optional.of(testService));

        // Act
        String result = spaBookingController.addSpaBooking(
                bookDate, note, serviceId, petName, petSpecies, petBreed, 
                petWeight, petAge, petProfileId, session, model
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(petProfileService).updatePetProfile(petProfileId, testPetProfile);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID16: Add Spa Booking with Invalid Pet Age")
    void testAddSpaBooking_InvalidPetAge() {
        // Arrange
        LocalDateTime bookDate = LocalDateTime.now().plusHours(2);
        String note = "Test note";
        Long serviceId = 1L;
        String petName = "Luna";
        String petSpecies = "Cat";
        String petBreed = "Persian";
        float petWeight = 10.0f;
        Integer petAge = -1; // Invalid negative age
        Long petProfileId = 1L;

        when(petProfileService.findById(petProfileId)).thenReturn(testPetProfile);
        when(spaBookingService.Service_findById(serviceId)).thenReturn(Optional.of(testService));

        // Act
        String result = spaBookingController.addSpaBooking(
                bookDate, note, serviceId, petName, petSpecies, petBreed, 
                petWeight, petAge, petProfileId, session, model
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(petProfileService).updatePetProfile(petProfileId, testPetProfile);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID17: Add Spa Booking with High Pet Age")
    void testAddSpaBooking_HighPetAge() {
        // Arrange
        LocalDateTime bookDate = LocalDateTime.now().plusHours(2);
        String note = "Test note";
        Long serviceId = 1L;
        String petName = "Luna";
        String petSpecies = "Cat";
        String petBreed = "Persian";
        float petWeight = 10.0f;
        Integer petAge = 301; // Invalid high age > 300
        Long petProfileId = 1L;

        when(petProfileService.findById(petProfileId)).thenReturn(testPetProfile);
        when(spaBookingService.Service_findById(serviceId)).thenReturn(Optional.of(testService));

        // Act
        String result = spaBookingController.addSpaBooking(
                bookDate, note, serviceId, petName, petSpecies, petBreed, 
                petWeight, petAge, petProfileId, session, model
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(petProfileService).updatePetProfile(petProfileId, testPetProfile);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID18: Add Spa Booking with Valid Pet Age")
    void testAddSpaBooking_ValidPetAge() {
        // Arrange
        LocalDateTime bookDate = LocalDateTime.now().plusHours(2);
        String note = "Test note";
        Long serviceId = 1L;
        String petName = "Luna";
        String petSpecies = "Cat";
        String petBreed = "Persian";
        float petWeight = 10.0f;
        Integer petAge = 5;
        Long petProfileId = 1L;

        when(petProfileService.findById(petProfileId)).thenReturn(testPetProfile);
        when(spaBookingService.Service_findById(serviceId)).thenReturn(Optional.of(testService));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(testSpaBooking);

        // Act
        String result = spaBookingController.addSpaBooking(
                bookDate, note, serviceId, petName, petSpecies, petBreed, 
                petWeight, petAge, petProfileId, session, model
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(petProfileService).updatePetProfile(petProfileId, testPetProfile);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID19: Add Spa Booking with Existing Service")
    void testAddSpaBooking_ExistingService() {
        // Arrange
        LocalDateTime bookDate = LocalDateTime.now().plusHours(2);
        String note = "Test note";
        Long serviceId = 1L;
        String petName = "Luna";
        String petSpecies = "Cat";
        String petBreed = "Persian";
        float petWeight = 10.0f;
        Integer petAge = 5;
        Long petProfileId = 1L;

        when(petProfileService.findById(petProfileId)).thenReturn(testPetProfile);
        when(spaBookingService.Service_findById(serviceId)).thenReturn(Optional.of(testService));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(testSpaBooking);

        // Act
        String result = spaBookingController.addSpaBooking(
                bookDate, note, serviceId, petName, petSpecies, petBreed, 
                petWeight, petAge, petProfileId, session, model
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(petProfileService).updatePetProfile(petProfileId, testPetProfile);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID20: Add Spa Booking with Non-existent Service")
    void testAddSpaBooking_NonExistentService() {
        // Arrange
        LocalDateTime bookDate = LocalDateTime.now().plusHours(2);
        String note = "Test note";
        Long serviceId = 999L; // Non-existent service
        String petName = "Luna";
        String petSpecies = "Cat";
        String petBreed = "Persian";
        float petWeight = 10.0f;
        Integer petAge = 5;
        Long petProfileId = 1L;

        when(petProfileService.findById(petProfileId)).thenReturn(testPetProfile);
        when(spaBookingService.Service_findById(serviceId)).thenReturn(Optional.empty());

        // Act
        String result = spaBookingController.addSpaBooking(
                bookDate, note, serviceId, petName, petSpecies, petBreed, 
                petWeight, petAge, petProfileId, session, model
        );

        // Assert
        assertEquals("error", result);
        verify(model).addAttribute("error", "Dịch vụ không tồn tại");
    }

    @Test
    @DisplayName("UTCID21: Add Spa Booking with Current Time")
    void testAddSpaBooking_CurrentTime() {
        // Arrange
        LocalDateTime bookDate = LocalDateTime.now(); // Current time
        String note = "Test note";
        Long serviceId = 1L;
        String petName = "Luna";
        String petSpecies = "Cat";
        String petBreed = "Persian";
        float petWeight = 10.0f;
        Integer petAge = 5;
        Long petProfileId = 1L;

        when(petProfileService.findById(petProfileId)).thenReturn(testPetProfile);
        when(spaBookingService.Service_findById(serviceId)).thenReturn(Optional.of(testService));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(testSpaBooking);

        // Act
        String result = spaBookingController.addSpaBooking(
                bookDate, note, serviceId, petName, petSpecies, petBreed, 
                petWeight, petAge, petProfileId, session, model
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(petProfileService).updatePetProfile(petProfileId, testPetProfile);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID22: Add Spa Booking with 2 Hours Later")
    void testAddSpaBooking_TwoHoursLater() {
        // Arrange
        LocalDateTime bookDate = LocalDateTime.now().plusHours(2); // 2 hours later
        String note = "Test note";
        Long serviceId = 1L;
        String petName = "Luna";
        String petSpecies = "Cat";
        String petBreed = "Persian";
        float petWeight = 10.0f;
        Integer petAge = 5;
        Long petProfileId = 1L;

        when(petProfileService.findById(petProfileId)).thenReturn(testPetProfile);
        when(spaBookingService.Service_findById(serviceId)).thenReturn(Optional.of(testService));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(testSpaBooking);

        // Act
        String result = spaBookingController.addSpaBooking(
                bookDate, note, serviceId, petName, petSpecies, petBreed, 
                petWeight, petAge, petProfileId, session, model
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(petProfileService).updatePetProfile(petProfileId, testPetProfile);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID23: Add Spa Booking with Unauthenticated User")
    void testAddSpaBooking_UnauthenticatedUser() {
        // Arrange
        session.removeAttribute("loggedInUser");
        LocalDateTime bookDate = LocalDateTime.now().plusHours(2);
        String note = "Test note";
        Long serviceId = 1L;
        String petName = "Luna";
        String petSpecies = "Cat";
        String petBreed = "Persian";
        float petWeight = 10.0f;
        Integer petAge = 5;
        Long petProfileId = 1L;

        // Act
        String result = spaBookingController.addSpaBooking(
                bookDate, note, serviceId, petName, petSpecies, petBreed, 
                petWeight, petAge, petProfileId, session, model
        );

        // Assert
        assertEquals("redirect:/login", result);
    }

    @Test
    @DisplayName("UTCID24: Add Spa Booking with Server Error")
    void testAddSpaBooking_ServerError() {
        // Arrange
        LocalDateTime bookDate = LocalDateTime.now().plusHours(2);
        String note = "Test note";
        Long serviceId = 1L;
        String petName = "Luna";
        String petSpecies = "Cat";
        String petBreed = "Persian";
        float petWeight = 10.0f;
        Integer petAge = 5;
        Long petProfileId = 1L;

        when(petProfileService.findById(petProfileId)).thenReturn(testPetProfile);
        when(spaBookingService.Service_findById(serviceId)).thenReturn(Optional.of(testService));
        when(spaBookingService.save(any(SpaBooking.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        String result = spaBookingController.addSpaBooking(
                bookDate, note, serviceId, petName, petSpecies, petBreed, 
                petWeight, petAge, petProfileId, session, model
        );

        // Assert
        assertEquals("error", result);
        verify(model).addAttribute("error", "Lỗi khi đặt lịch: Database error");
    }
}
