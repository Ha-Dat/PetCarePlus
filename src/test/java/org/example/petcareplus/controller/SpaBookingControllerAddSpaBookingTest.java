package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.PetProfile;
import org.example.petcareplus.entity.Profile;
import org.example.petcareplus.entity.Service;
import org.example.petcareplus.entity.SpaBooking;
import org.example.petcareplus.enums.AccountRole;
import org.example.petcareplus.enums.BookingStatus;
import org.example.petcareplus.enums.ServiceCategory;
import org.example.petcareplus.service.PetProfileService;
import org.example.petcareplus.service.SpaBookingService;
import org.example.petcareplus.service.ServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpaBookingControllerAddSpaBookingTest {

    @Mock
    private SpaBookingService spaBookingService;

    @Mock
    private PetProfileService petProfileService;

    @Mock
    private ServiceService serviceService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private HttpSession session;

    @InjectMocks
    private SpaBookingController spaBookingController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(spaBookingController).build();
    }

    // ========== ADD SPA BOOKING() METHOD TEST CASES ==========
    
    @Test
    @DisplayName("UTCID43: Add Spa Booking with NULL Pet Name")
    void testAddSpaBooking_NullPetName_Success() {
        // Arrange
        Account account = createMockAccount();
        PetProfile petProfile = createMockPetProfile();
        Service service = createMockService();
        
        when(session.getAttribute("loggedInUser")).thenReturn(account);
        when(petProfileService.findById(1L)).thenReturn(petProfile);
        when(spaBookingService.Service_findById(1L)).thenReturn(Optional.of(service));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(new SpaBooking());

        // Act
        String result = spaBookingController.addSpaBooking(
                LocalDateTime.now().plusHours(3), // bookDate
                "Test note", // note
                1L, // serviceId
                null, // petName - NULL
                "Cat", // petSpecies
                "Persian", // petBreed
                10.0f, // petWeight
                5, // petAge
                1L, // petProfileId
                session,
                model,
                redirectAttributes
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID44: Add Spa Booking with Valid Pet Name")
    void testAddSpaBooking_ValidPetName_Success() {
        // Arrange
        Account account = createMockAccount();
        PetProfile petProfile = createMockPetProfile();
        Service service = createMockService();
        
        when(session.getAttribute("loggedInUser")).thenReturn(account);
        when(petProfileService.findById(1L)).thenReturn(petProfile);
        when(spaBookingService.Service_findById(1L)).thenReturn(Optional.of(service));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(new SpaBooking());

        // Act
        String result = spaBookingController.addSpaBooking(
                LocalDateTime.now().plusHours(3), // bookDate
                "Test note", // note
                1L, // serviceId
                "Luna", // petName - Valid
                "Cat", // petSpecies
                "Persian", // petBreed
                10.0f, // petWeight
                5, // petAge
                1L, // petProfileId
                session,
                model,
                redirectAttributes
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID45: Add Spa Booking with NULL Pet Species")
    void testAddSpaBooking_NullPetSpecies_Success() {
        // Arrange
        Account account = createMockAccount();
        PetProfile petProfile = createMockPetProfile();
        Service service = createMockService();
        
        when(session.getAttribute("loggedInUser")).thenReturn(account);
        when(petProfileService.findById(1L)).thenReturn(petProfile);
        when(spaBookingService.Service_findById(1L)).thenReturn(Optional.of(service));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(new SpaBooking());

        // Act
        String result = spaBookingController.addSpaBooking(
                LocalDateTime.now().plusHours(3), // bookDate
                "Test note", // note
                1L, // serviceId
                "Luna", // petName
                null, // petSpecies - NULL
                "Persian", // petBreed
                10.0f, // petWeight
                5, // petAge
                1L, // petProfileId
                session,
                model,
                redirectAttributes
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID46: Add Spa Booking with Invalid Pet Species")
    void testAddSpaBooking_InvalidPetSpecies_Success() {
        // Arrange
        Account account = createMockAccount();
        PetProfile petProfile = createMockPetProfile();
        Service service = createMockService();
        
        when(session.getAttribute("loggedInUser")).thenReturn(account);
        when(petProfileService.findById(1L)).thenReturn(petProfile);
        when(spaBookingService.Service_findById(1L)).thenReturn(Optional.of(service));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(new SpaBooking());

        // Act
        String result = spaBookingController.addSpaBooking(
                LocalDateTime.now().plusHours(3), // bookDate
                "Test note", // note
                1L, // serviceId
                "Luna", // petName
                "1234", // petSpecies - Invalid
                "Persian", // petBreed
                10.0f, // petWeight
                5, // petAge
                1L, // petProfileId
                session,
                model,
                redirectAttributes
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID47: Add Spa Booking with Valid Pet Species")
    void testAddSpaBooking_ValidPetSpecies_Success() {
        // Arrange
        Account account = createMockAccount();
        PetProfile petProfile = createMockPetProfile();
        Service service = createMockService();
        
        when(session.getAttribute("loggedInUser")).thenReturn(account);
        when(petProfileService.findById(1L)).thenReturn(petProfile);
        when(spaBookingService.Service_findById(1L)).thenReturn(Optional.of(service));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(new SpaBooking());

        // Act
        String result = spaBookingController.addSpaBooking(
                LocalDateTime.now().plusHours(3), // bookDate
                "Test note", // note
                1L, // serviceId
                "Luna", // petName
                "Cat", // petSpecies - Valid
                "Persian", // petBreed
                10.0f, // petWeight
                5, // petAge
                1L, // petProfileId
                session,
                model,
                redirectAttributes
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID48: Add Spa Booking with NULL Pet Breed")
    void testAddSpaBooking_NullPetBreed_Success() {
        // Arrange
        Account account = createMockAccount();
        PetProfile petProfile = createMockPetProfile();
        Service service = createMockService();
        
        when(session.getAttribute("loggedInUser")).thenReturn(account);
        when(petProfileService.findById(1L)).thenReturn(petProfile);
        when(spaBookingService.Service_findById(1L)).thenReturn(Optional.of(service));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(new SpaBooking());

        // Act
        String result = spaBookingController.addSpaBooking(
                LocalDateTime.now().plusHours(3), // bookDate
                "Test note", // note
                1L, // serviceId
                "Luna", // petName
                "Cat", // petSpecies
                null, // petBreed - NULL
                10.0f, // petWeight
                5, // petAge
                1L, // petProfileId
                session,
                model,
                redirectAttributes
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID49: Add Spa Booking with Invalid Pet Breed")
    void testAddSpaBooking_InvalidPetBreed_Success() {
        // Arrange
        Account account = createMockAccount();
        PetProfile petProfile = createMockPetProfile();
        Service service = createMockService();
        
        when(session.getAttribute("loggedInUser")).thenReturn(account);
        when(petProfileService.findById(1L)).thenReturn(petProfile);
        when(spaBookingService.Service_findById(1L)).thenReturn(Optional.of(service));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(new SpaBooking());

        // Act
        String result = spaBookingController.addSpaBooking(
                LocalDateTime.now().plusHours(3), // bookDate
                "Test note", // note
                1L, // serviceId
                "Luna", // petName
                "Cat", // petSpecies
                "1234", // petBreed - Invalid
                10.0f, // petWeight
                5, // petAge
                1L, // petProfileId
                session,
                model,
                redirectAttributes
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID50: Add Spa Booking with Valid Pet Breed")
    void testAddSpaBooking_ValidPetBreed_Success() {
        // Arrange
        Account account = createMockAccount();
        PetProfile petProfile = createMockPetProfile();
        Service service = createMockService();
        
        when(session.getAttribute("loggedInUser")).thenReturn(account);
        when(petProfileService.findById(1L)).thenReturn(petProfile);
        when(spaBookingService.Service_findById(1L)).thenReturn(Optional.of(service));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(new SpaBooking());

        // Act
        String result = spaBookingController.addSpaBooking(
                LocalDateTime.now().plusHours(3), // bookDate
                "Test note", // note
                1L, // serviceId
                "Luna", // petName
                "Cat", // petSpecies
                "Persian", // petBreed - Valid
                10.0f, // petWeight
                5, // petAge
                1L, // petProfileId
                session,
                model,
                redirectAttributes
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID51: Add Spa Booking with Zero Weight")
    void testAddSpaBooking_ZeroWeight_Success() {
        // Arrange
        Account account = createMockAccount();
        PetProfile petProfile = createMockPetProfile();
        Service service = createMockService();
        
        when(session.getAttribute("loggedInUser")).thenReturn(account);
        when(petProfileService.findById(1L)).thenReturn(petProfile);
        when(spaBookingService.Service_findById(1L)).thenReturn(Optional.of(service));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(new SpaBooking());

        // Act
        String result = spaBookingController.addSpaBooking(
                LocalDateTime.now().plusHours(3), // bookDate
                "Test note", // note
                1L, // serviceId
                "Luna", // petName
                "Cat", // petSpecies
                "Persian", // petBreed
                0.0f, // petWeight - Zero
                5, // petAge
                1L, // petProfileId
                session,
                model,
                redirectAttributes
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID52: Add Spa Booking with Negative Weight")
    void testAddSpaBooking_NegativeWeight_Success() {
        // Arrange
        Account account = createMockAccount();
        PetProfile petProfile = createMockPetProfile();
        Service service = createMockService();
        
        when(session.getAttribute("loggedInUser")).thenReturn(account);
        when(petProfileService.findById(1L)).thenReturn(petProfile);
        when(spaBookingService.Service_findById(1L)).thenReturn(Optional.of(service));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(new SpaBooking());

        // Act
        String result = spaBookingController.addSpaBooking(
                LocalDateTime.now().plusHours(3), // bookDate
                "Test note", // note
                1L, // serviceId
                "Luna", // petName
                "Cat", // petSpecies
                "Persian", // petBreed
                -1.0f, // petWeight - Negative
                5, // petAge
                1L, // petProfileId
                session,
                model,
                redirectAttributes
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID53: Add Spa Booking with Invalid Weight Format")
    void testAddSpaBooking_InvalidWeightFormat_Success() {
        // Arrange
        Account account = createMockAccount();
        PetProfile petProfile = createMockPetProfile();
        Service service = createMockService();
        
        when(session.getAttribute("loggedInUser")).thenReturn(account);
        when(petProfileService.findById(1L)).thenReturn(petProfile);
        when(spaBookingService.Service_findById(1L)).thenReturn(Optional.of(service));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(new SpaBooking());

        // Act
        String result = spaBookingController.addSpaBooking(
                LocalDateTime.now().plusHours(3), // bookDate
                "Test note", // note
                1L, // serviceId
                "Luna", // petName
                "Cat", // petSpecies
                "Persian", // petBreed
                999.0f, // petWeight - Invalid format (too high)
                5, // petAge
                1L, // petProfileId
                session,
                model,
                redirectAttributes
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID54: Add Spa Booking with Weight Too High")
    void testAddSpaBooking_WeightTooHigh_Success() {
        // Arrange
        Account account = createMockAccount();
        PetProfile petProfile = createMockPetProfile();
        Service service = createMockService();
        
        when(session.getAttribute("loggedInUser")).thenReturn(account);
        when(petProfileService.findById(1L)).thenReturn(petProfile);
        when(spaBookingService.Service_findById(1L)).thenReturn(Optional.of(service));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(new SpaBooking());

        // Act
        String result = spaBookingController.addSpaBooking(
                LocalDateTime.now().plusHours(3), // bookDate
                "Test note", // note
                1L, // serviceId
                "Luna", // petName
                "Cat", // petSpecies
                "Persian", // petBreed
                101.0f, // petWeight - Too high
                5, // petAge
                1L, // petProfileId
                session,
                model,
                redirectAttributes
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID55: Add Spa Booking with Valid Weight")
    void testAddSpaBooking_ValidWeight_Success() {
        // Arrange
        Account account = createMockAccount();
        PetProfile petProfile = createMockPetProfile();
        Service service = createMockService();
        
        when(session.getAttribute("loggedInUser")).thenReturn(account);
        when(petProfileService.findById(1L)).thenReturn(petProfile);
        when(spaBookingService.Service_findById(1L)).thenReturn(Optional.of(service));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(new SpaBooking());

        // Act
        String result = spaBookingController.addSpaBooking(
                LocalDateTime.now().plusHours(3), // bookDate
                "Test note", // note
                1L, // serviceId
                "Luna", // petName
                "Cat", // petSpecies
                "Persian", // petBreed
                10.0f, // petWeight - Valid
                5, // petAge
                1L, // petProfileId
                session,
                model,
                redirectAttributes
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID56: Add Spa Booking with Zero Age")
    void testAddSpaBooking_ZeroAge_Success() {
        // Arrange
        Account account = createMockAccount();
        PetProfile petProfile = createMockPetProfile();
        Service service = createMockService();
        
        when(session.getAttribute("loggedInUser")).thenReturn(account);
        when(petProfileService.findById(1L)).thenReturn(petProfile);
        when(spaBookingService.Service_findById(1L)).thenReturn(Optional.of(service));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(new SpaBooking());

        // Act
        String result = spaBookingController.addSpaBooking(
                LocalDateTime.now().plusHours(3), // bookDate
                "Test note", // note
                1L, // serviceId
                "Luna", // petName
                "Cat", // petSpecies
                "Persian", // petBreed
                10.0f, // petWeight
                0, // petAge - Zero
                1L, // petProfileId
                session,
                model,
                redirectAttributes
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID57: Add Spa Booking with Invalid Age Format")
    void testAddSpaBooking_InvalidAgeFormat_Success() {
        // Arrange
        Account account = createMockAccount();
        PetProfile petProfile = createMockPetProfile();
        Service service = createMockService();
        
        when(session.getAttribute("loggedInUser")).thenReturn(account);
        when(petProfileService.findById(1L)).thenReturn(petProfile);
        when(spaBookingService.Service_findById(1L)).thenReturn(Optional.of(service));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(new SpaBooking());

        // Act
        String result = spaBookingController.addSpaBooking(
                LocalDateTime.now().plusHours(3), // bookDate
                "Test note", // note
                1L, // serviceId
                "Luna", // petName
                "Cat", // petSpecies
                "Persian", // petBreed
                10.0f, // petWeight
                999, // petAge - Invalid format (too high)
                1L, // petProfileId
                session,
                model,
                redirectAttributes
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID58: Add Spa Booking with Negative Age")
    void testAddSpaBooking_NegativeAge_Success() {
        // Arrange
        Account account = createMockAccount();
        PetProfile petProfile = createMockPetProfile();
        Service service = createMockService();
        
        when(session.getAttribute("loggedInUser")).thenReturn(account);
        when(petProfileService.findById(1L)).thenReturn(petProfile);
        when(spaBookingService.Service_findById(1L)).thenReturn(Optional.of(service));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(new SpaBooking());

        // Act
        String result = spaBookingController.addSpaBooking(
                LocalDateTime.now().plusHours(3), // bookDate
                "Test note", // note
                1L, // serviceId
                "Luna", // petName
                "Cat", // petSpecies
                "Persian", // petBreed
                10.0f, // petWeight
                -1, // petAge - Negative
                1L, // petProfileId
                session,
                model,
                redirectAttributes
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID59: Add Spa Booking with Age Too High")
    void testAddSpaBooking_AgeTooHigh_Success() {
        // Arrange
        Account account = createMockAccount();
        PetProfile petProfile = createMockPetProfile();
        Service service = createMockService();
        
        when(session.getAttribute("loggedInUser")).thenReturn(account);
        when(petProfileService.findById(1L)).thenReturn(petProfile);
        when(spaBookingService.Service_findById(1L)).thenReturn(Optional.of(service));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(new SpaBooking());

        // Act
        String result = spaBookingController.addSpaBooking(
                LocalDateTime.now().plusHours(3), // bookDate
                "Test note", // note
                1L, // serviceId
                "Luna", // petName
                "Cat", // petSpecies
                "Persian", // petBreed
                10.0f, // petWeight
                301, // petAge - Too high
                1L, // petProfileId
                session,
                model,
                redirectAttributes
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID60: Add Spa Booking with Valid Age")
    void testAddSpaBooking_ValidAge_Success() {
        // Arrange
        Account account = createMockAccount();
        PetProfile petProfile = createMockPetProfile();
        Service service = createMockService();
        
        when(session.getAttribute("loggedInUser")).thenReturn(account);
        when(petProfileService.findById(1L)).thenReturn(petProfile);
        when(spaBookingService.Service_findById(1L)).thenReturn(Optional.of(service));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(new SpaBooking());

        // Act
        String result = spaBookingController.addSpaBooking(
                LocalDateTime.now().plusHours(3), // bookDate
                "Test note", // note
                1L, // serviceId
                "Luna", // petName
                "Cat", // petSpecies
                "Persian", // petBreed
                10.0f, // petWeight
                5, // petAge - Valid
                1L, // petProfileId
                session,
                model,
                redirectAttributes
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID61: Add Spa Booking with Non-existent Service")
    void testAddSpaBooking_NonExistentService_Error() {
        // Arrange
        Account account = createMockAccount();
        PetProfile petProfile = createMockPetProfile();
        
        when(session.getAttribute("loggedInUser")).thenReturn(account);
        when(petProfileService.findById(1L)).thenReturn(petProfile);
        when(spaBookingService.Service_findById(999L)).thenReturn(Optional.empty());

        // Act
        String result = spaBookingController.addSpaBooking(
                LocalDateTime.now().plusHours(3), // bookDate
                "Test note", // note
                999L, // serviceId - Non-existent
                "Luna", // petName
                "Cat", // petSpecies
                "Persian", // petBreed
                10.0f, // petWeight
                5, // petAge
                1L, // petProfileId
                session,
                model,
                redirectAttributes
        );

        // Assert
        assertEquals("error", result);
        verify(model).addAttribute("error", "Dịch vụ không tồn tại");
    }

    @Test
    @DisplayName("UTCID62: Add Spa Booking with Current Time")
    void testAddSpaBooking_CurrentTime_Success() {
        // Arrange
        Account account = createMockAccount();
        PetProfile petProfile = createMockPetProfile();
        Service service = createMockService();
        
        when(session.getAttribute("loggedInUser")).thenReturn(account);
        when(petProfileService.findById(1L)).thenReturn(petProfile);
        when(spaBookingService.Service_findById(1L)).thenReturn(Optional.of(service));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(new SpaBooking());

        // Act
        String result = spaBookingController.addSpaBooking(
                LocalDateTime.now(), // bookDate - Current time
                "Test note", // note
                1L, // serviceId
                "Luna", // petName
                "Cat", // petSpecies
                "Persian", // petBreed
                10.0f, // petWeight
                5, // petAge
                1L, // petProfileId
                session,
                model,
                redirectAttributes
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID63: Add Spa Booking with Valid Time")
    void testAddSpaBooking_ValidTime_Success() {
        // Arrange
        Account account = createMockAccount();
        PetProfile petProfile = createMockPetProfile();
        Service service = createMockService();
        
        when(session.getAttribute("loggedInUser")).thenReturn(account);
        when(petProfileService.findById(1L)).thenReturn(petProfile);
        when(spaBookingService.Service_findById(1L)).thenReturn(Optional.of(service));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(new SpaBooking());

        // Act
        String result = spaBookingController.addSpaBooking(
                LocalDateTime.now().plusHours(3), // bookDate - Valid time
                "Test note", // note
                1L, // serviceId
                "Luna", // petName
                "Cat", // petSpecies
                "Persian", // petBreed
                10.0f, // petWeight
                5, // petAge
                1L, // petProfileId
                session,
                model,
                redirectAttributes
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID64: Add Spa Booking with Non-existent Service and Current Time")
    void testAddSpaBooking_NonExistentServiceAndCurrentTime_Error() {
        // Arrange
        Account account = createMockAccount();
        PetProfile petProfile = createMockPetProfile();
        
        when(session.getAttribute("loggedInUser")).thenReturn(account);
        when(petProfileService.findById(1L)).thenReturn(petProfile);
        when(spaBookingService.Service_findById(999L)).thenReturn(Optional.empty());

        // Act
        String result = spaBookingController.addSpaBooking(
                LocalDateTime.now(), // bookDate - Current time
                "Test note", // note
                999L, // serviceId - Non-existent
                "Luna", // petName
                "Cat", // petSpecies
                "Persian", // petBreed
                10.0f, // petWeight
                5, // petAge
                1L, // petProfileId
                session,
                model,
                redirectAttributes
        );

        // Assert
        assertEquals("error", result);
        verify(model).addAttribute("error", "Dịch vụ không tồn tại");
    }

    @Test
    @DisplayName("UTCID65: Add Spa Booking with Valid Service and 2 Hours Later")
    void testAddSpaBooking_ValidServiceAnd2HoursLater_Success() {
        // Arrange
        Account account = createMockAccount();
        PetProfile petProfile = createMockPetProfile();
        Service service = createMockService();
        
        when(session.getAttribute("loggedInUser")).thenReturn(account);
        when(petProfileService.findById(1L)).thenReturn(petProfile);
        when(spaBookingService.Service_findById(1L)).thenReturn(Optional.of(service));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(new SpaBooking());

        // Act
        String result = spaBookingController.addSpaBooking(
                LocalDateTime.now().plusHours(2), // bookDate - 2 hours later
                "Test note", // note
                1L, // serviceId
                "Luna", // petName
                "Cat", // petSpecies
                "Persian", // petBreed
                10.0f, // petWeight
                5, // petAge
                1L, // petProfileId
                session,
                model,
                redirectAttributes
        );

        // Assert
        assertEquals("redirect:/spa-booking-form", result);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID66: Add Spa Booking with Non-existent Service and 2 Hours Later")
    void testAddSpaBooking_NonExistentServiceAnd2HoursLater_Error() {
        // Arrange
        Account account = createMockAccount();
        PetProfile petProfile = createMockPetProfile();
        
        when(session.getAttribute("loggedInUser")).thenReturn(account);
        when(petProfileService.findById(1L)).thenReturn(petProfile);
        when(spaBookingService.Service_findById(999L)).thenReturn(Optional.empty());

        // Act
        String result = spaBookingController.addSpaBooking(
                LocalDateTime.now().plusHours(2), // bookDate - 2 hours later
                "Test note", // note
                999L, // serviceId - Non-existent
                "Luna", // petName
                "Cat", // petSpecies
                "Persian", // petBreed
                10.0f, // petWeight
                5, // petAge
                1L, // petProfileId
                session,
                model,
                redirectAttributes
        );

        // Assert
        assertEquals("error", result);
        verify(model).addAttribute("error", "Dịch vụ không tồn tại");
    }

    // Helper methods
    private Account createMockAccount() {
        Account account = new Account();
        account.setAccountId(1L);
        account.setRole(AccountRole.CUSTOMER);
        
        Profile profile = new Profile();
        profile.setProfileId(1L);
        account.setProfile(profile);
        
        return account;
    }

    private PetProfile createMockPetProfile() {
        PetProfile petProfile = new PetProfile();
        petProfile.setPetProfileId(1L);
        petProfile.setName("Luna");
        petProfile.setSpecies("Cat");
        petProfile.setBreeds("Persian");
        petProfile.setWeight(10.0f);
        petProfile.setAge(5);
        
        Profile profile = new Profile();
        profile.setProfileId(1L);
        petProfile.setProfile(profile);
        
        return petProfile;
    }

    private Service createMockService() {
        Service service = new Service();
        service.setServiceId(1L);
        service.setName("Spa Service");
        service.setServiceCategory(ServiceCategory.SPA);
        service.setPrice(BigDecimal.valueOf(100000));
        service.setDuration(30);
        return service;
    }
}
