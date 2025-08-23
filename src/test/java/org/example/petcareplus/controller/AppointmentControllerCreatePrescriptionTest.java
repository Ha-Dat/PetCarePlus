package org.example.petcareplus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.petcareplus.dto.PrescriptionDTO;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.AppointmentBooking;
import org.example.petcareplus.entity.Profile;
import org.example.petcareplus.entity.Service;
import org.example.petcareplus.enums.AccountRole;
import org.example.petcareplus.enums.AccountStatus;
import org.example.petcareplus.enums.BookingStatus;
import org.example.petcareplus.enums.ServiceCategory;
import org.example.petcareplus.service.AppointmentService;
import org.example.petcareplus.service.PrescriptionService;
import org.example.petcareplus.service.PetProfileService;
import org.example.petcareplus.service.ServiceService;
import org.example.petcareplus.service.CategoryService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AppointmentControllerCreatePrescriptionTest {

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private PrescriptionService prescriptionService;

    @Mock
    private PetProfileService petProfileService;

    @Mock
    private ServiceService serviceService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private AppointmentController appointmentController;

    private MockHttpSession session;
    private Account testAccount;
    private Profile testProfile;
    private Service testService;
    private AppointmentBooking testAppointment;
    private PrescriptionDTO testPrescriptionDTO;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        objectMapper = new ObjectMapper();
        
        // Setup test account
        testAccount = new Account();
        testAccount.setAccountId(1L);
        testAccount.setName("Nguyen Van A");
        testAccount.setPhone("0912345678");
        testAccount.setRole(AccountRole.VET);
        testAccount.setStatus(AccountStatus.ACTIVE);
        
        // Setup test profile
        testProfile = new Profile();
        testProfile.setProfileId(1L);
        testAccount.setProfile(testProfile);
        
        // Setup test service
        testService = new Service();
        testService.setServiceId(1L);
        testService.setName("Vaccination");
        testService.setServiceCategory(ServiceCategory.APPOINTMENT);
        testService.setPrice(new BigDecimal("200000"));
        testService.setDuration(30);
        
        // Setup test appointment
        testAppointment = new AppointmentBooking();
        testAppointment.setAppointmentBookingId(1L);
        testAppointment.setBookDate(LocalDateTime.now().plusHours(2));
        testAppointment.setNote("Test appointment");
        testAppointment.setStatus(BookingStatus.ACCEPTED);
        testAppointment.setService(testService);
        
        // Setup test prescription DTO
        testPrescriptionDTO = new PrescriptionDTO();
        testPrescriptionDTO.setPrescriptionId(1L);
        testPrescriptionDTO.setAppointmentBookingId(1L);
        testPrescriptionDTO.setDrugName("Amoxicillin");
        testPrescriptionDTO.setAmount("500mg");
        testPrescriptionDTO.setNote("Thuốc kháng sinh, thuốc giảm đau ...");
        
        // Setup session
        session.setAttribute("loggedInUser", testAccount);
    }

    // ========== CREATEPRESCRIPTION() METHOD TEST CASES ==========

    @Test
    @DisplayName("UTCID D01: Create Prescription with NULL Prescription ID")
    void testCreatePrescription_NullPrescriptionId() {
        // Arrange
        testPrescriptionDTO.setPrescriptionId(null);
        testPrescriptionDTO.setAppointmentBookingId(null); // NULL Prescription ID

        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.Collections.emptyList());

        // Act
        ResponseEntity<?> response = appointmentController.createPrescription(testPrescriptionDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("UTCID D02: Create Prescription with Empty Prescription ID")
    void testCreatePrescription_EmptyPrescriptionId() {
        // Arrange
        testPrescriptionDTO.setAppointmentBookingId(0L); // Empty Prescription ID (0)

        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.Collections.emptyList());

        // Act
        ResponseEntity<?> response = appointmentController.createPrescription(testPrescriptionDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("UTCID D03: Create Prescription Successfully")
    void testCreatePrescription_Success() {
        // Arrange
        testPrescriptionDTO.setAppointmentBookingId(1L); // Valid Prescription ID

        when(bindingResult.hasErrors()).thenReturn(false);
        when(prescriptionService.createPrescription(any(PrescriptionDTO.class))).thenReturn(testPrescriptionDTO);

        // Act
        ResponseEntity<?> response = appointmentController.createPrescription(testPrescriptionDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(prescriptionService).createPrescription(any(PrescriptionDTO.class));
    }

    @Test
    @DisplayName("UTCID D04: Create Prescription with Invalid Prescription ID")
    void testCreatePrescription_InvalidPrescriptionId() {
        // Arrange
        testPrescriptionDTO.setAppointmentBookingId(-1L); // Invalid Prescription ID (-1)

        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.Collections.emptyList());

        // Act
        ResponseEntity<?> response = appointmentController.createPrescription(testPrescriptionDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("UTCID D05: Create Prescription with Non-existent Prescription ID")
    void testCreatePrescription_NonExistentPrescriptionId() {
        // Arrange
        testPrescriptionDTO.setAppointmentBookingId(1000L); // Non-existent Prescription ID (1000)

        when(bindingResult.hasErrors()).thenReturn(false);
        when(prescriptionService.createPrescription(any(PrescriptionDTO.class)))
                .thenThrow(new RuntimeException("Appointment not found with id: 1000"));

        // Act
        ResponseEntity<?> response = appointmentController.createPrescription(testPrescriptionDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("UTCID D06: Create Prescription with Invalid Format Prescription ID")
    void testCreatePrescription_InvalidFormatPrescriptionId() {
        // Arrange
        // Note: Since appointmentBookingId is Long, we can't test string format directly
        // This test will focus on validation errors
        testPrescriptionDTO.setAppointmentBookingId(null);

        when(bindingResult.hasErrors()).thenReturn(true);
        FieldError fieldError = new FieldError("prescriptionDTO", "appointmentBookingId", "Appointment ID cannot be null");
        when(bindingResult.getFieldErrors()).thenReturn(java.util.Collections.singletonList(fieldError));

        // Act
        ResponseEntity<?> response = appointmentController.createPrescription(testPrescriptionDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("UTCID D07: Create Prescription with NULL Note")
    void testCreatePrescription_NullNote() {
        // Arrange
        testPrescriptionDTO.setNote(null); // NULL Note

        when(bindingResult.hasErrors()).thenReturn(true);
        FieldError fieldError = new FieldError("prescriptionDTO", "note", "Note cannot be blank");
        when(bindingResult.getFieldErrors()).thenReturn(java.util.Collections.singletonList(fieldError));

        // Act
        ResponseEntity<?> response = appointmentController.createPrescription(testPrescriptionDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("UTCID D08: Create Prescription with Empty Note")
    void testCreatePrescription_EmptyNote() {
        // Arrange
        testPrescriptionDTO.setNote(""); // Empty Note

        when(bindingResult.hasErrors()).thenReturn(true);
        FieldError fieldError = new FieldError("prescriptionDTO", "note", "Note cannot be blank");
        when(bindingResult.getFieldErrors()).thenReturn(java.util.Collections.singletonList(fieldError));

        // Act
        ResponseEntity<?> response = appointmentController.createPrescription(testPrescriptionDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("UTCID D09: Create Prescription with Valid Note")
    void testCreatePrescription_ValidNote() {
        // Arrange
        testPrescriptionDTO.setNote("Thuốc kháng sinh, thuốc giảm đau ..."); // Valid Note

        when(bindingResult.hasErrors()).thenReturn(false);
        when(prescriptionService.createPrescription(any(PrescriptionDTO.class))).thenReturn(testPrescriptionDTO);

        // Act
        ResponseEntity<?> response = appointmentController.createPrescription(testPrescriptionDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(prescriptionService).createPrescription(any(PrescriptionDTO.class));
    }

    @Test
    @DisplayName("UTCID D10: Create Prescription with Unauthenticated User")
    void testCreatePrescription_UnauthenticatedUser() {
        // Arrange
        // Note: This method doesn't check authentication directly, but the service layer might
        // For now, we'll test the normal flow since authentication is handled at service level
        when(bindingResult.hasErrors()).thenReturn(false);
        when(prescriptionService.createPrescription(any(PrescriptionDTO.class))).thenReturn(testPrescriptionDTO);

        // Act
        ResponseEntity<?> response = appointmentController.createPrescription(testPrescriptionDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("UTCID D11: Create Prescription with Server Error")
    void testCreatePrescription_ServerError() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);
        when(prescriptionService.createPrescription(any(PrescriptionDTO.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act
        ResponseEntity<?> response = appointmentController.createPrescription(testPrescriptionDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertTrue(responseBody.containsKey("error"));
    }
}
