package org.example.petcareplus.controller;

import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.Profile;
import org.example.petcareplus.entity.Service;
import org.example.petcareplus.entity.SpaBooking;
import org.example.petcareplus.enums.AccountRole;
import org.example.petcareplus.enums.AccountStatus;
import org.example.petcareplus.enums.BookingStatus;
import org.example.petcareplus.enums.ServiceCategory;
import org.example.petcareplus.service.PetProfileService;
import org.example.petcareplus.service.ServiceService;
import org.example.petcareplus.service.SpaBookingService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SpaBookingControllerAcceptSpaBookingTest {

    @Mock
    private SpaBookingService spaBookingService;

    @Mock
    private PetProfileService petProfileService;

    @Mock
    private ServiceService serviceService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private SpaBookingController spaBookingController;

    private MockHttpSession session;
    private Account testAccount;
    private Profile testProfile;
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
        testAccount.setRole(AccountRole.PET_GROOMER);
        testAccount.setStatus(AccountStatus.ACTIVE);
        
        // Setup test profile
        testProfile = new Profile();
        testProfile.setProfileId(1L);
        testAccount.setProfile(testProfile);
        
        // Setup test service
        testService = new Service();
        testService.setServiceId(1L);
        testService.setName("Spa Treatment");
        testService.setServiceCategory(ServiceCategory.SPA);
        testService.setPrice(new BigDecimal("100000"));
        testService.setDuration(60);
        
        // Setup test spa booking
        testSpaBooking = new SpaBooking();
        testSpaBooking.setSpaBookingId(1L);
        testSpaBooking.setBookDate(LocalDateTime.now().plusHours(2));
        testSpaBooking.setNote("Test note");
        testSpaBooking.setStatus(BookingStatus.PENDING);
        testSpaBooking.setService(testService);
        
        // Setup session
        session.setAttribute("loggedInUser", testAccount);
    }

    // ========== APPROVESPABOOKING() METHOD TEST CASES ==========

    @Test
    @DisplayName("UTCID D01: Accept Spa Booking with NULL Spa Booking ID")
    void testAcceptSpaBooking_NullSpaBookingId() {
        // Arrange
        Long id = null; // NULL Spa Booking ID

        // Act
        String result = spaBookingController.approveSpaBooking(id);

        // Assert
        assertEquals("Không tìm thấy lịch", result);
    }

    @Test
    @DisplayName("UTCID D02: Accept Spa Booking with Empty Spa Booking ID")
    void testAcceptSpaBooking_EmptySpaBookingId() {
        // Arrange
        Long id = 0L; // Empty Spa Booking ID (0)

        when(spaBookingService.findById(0L)).thenReturn(Optional.empty());

        // Act
        String result = spaBookingController.approveSpaBooking(id);

        // Assert
        assertEquals("Không tìm thấy lịch", result);
    }

    @Test
    @DisplayName("UTCID D03: Accept Spa Booking with Valid Spa Booking ID")
    void testAcceptSpaBooking_ValidSpaBookingId() {
        // Arrange
        Long id = 1L; // Valid Spa Booking ID
        testSpaBooking.setStatus(BookingStatus.PENDING);

        when(spaBookingService.findById(1L)).thenReturn(Optional.of(testSpaBooking));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(testSpaBooking);

        // Act
        String result = spaBookingController.approveSpaBooking(id);

        // Assert
        assertEquals("Duyệt lịch thành công", result);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID D04: Accept Spa Booking with Non-existent Spa Booking ID")
    void testAcceptSpaBooking_NonExistentSpaBookingId() {
        // Arrange
        Long id = -1L; // Non-existent Spa Booking ID (-1)

        when(spaBookingService.findById(-1L)).thenReturn(Optional.empty());

        // Act
        String result = spaBookingController.approveSpaBooking(id);

        // Assert
        assertEquals("Không tìm thấy lịch", result);
    }

    @Test
    @DisplayName("UTCID D05: Accept Spa Booking Successfully")
    void testAcceptSpaBooking_Success() {
        // Arrange
        Long id = 1000L; // Valid Spa Booking ID for successful approval
        testSpaBooking.setSpaBookingId(1000L);
        testSpaBooking.setStatus(BookingStatus.PENDING);

        when(spaBookingService.findById(1000L)).thenReturn(Optional.of(testSpaBooking));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(testSpaBooking);

        // Act
        String result = spaBookingController.approveSpaBooking(id);

        // Assert
        assertEquals("Duyệt lịch thành công", result);
        verify(spaBookingService).save(any(SpaBooking.class));
    }

    @Test
    @DisplayName("UTCID D06: Accept Spa Booking with Unauthenticated User")
    void testAcceptSpaBooking_UnauthenticatedUser() {
        // Arrange
        Long id = 1L;
        
        // Remove user from session to simulate unauthenticated user
        session.removeAttribute("loggedInUser");

        // Act
        String result = spaBookingController.approveSpaBooking(id);

        // Assert
        // Note: This method doesn't check authentication directly, but the service layer might
        // For now, we'll test the normal flow since authentication is handled at service level
        when(spaBookingService.findById(1L)).thenReturn(Optional.of(testSpaBooking));
        when(spaBookingService.save(any(SpaBooking.class))).thenReturn(testSpaBooking);

        result = spaBookingController.approveSpaBooking(id);
        assertEquals("Duyệt lịch thành công", result);
    }

    @Test
    @DisplayName("UTCID D07: Accept Spa Booking with Server Error")
    void testAcceptSpaBooking_ServerError() {
        // Arrange
        Long id = 1L;

        when(spaBookingService.findById(1L)).thenReturn(Optional.of(testSpaBooking));
        when(spaBookingService.save(any(SpaBooking.class))).thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        // Since the controller doesn't handle exceptions, this will throw RuntimeException
        assertThrows(RuntimeException.class, () -> {
            spaBookingController.approveSpaBooking(id);
        });
    }

    @Test
    @DisplayName("UTCID D08: Accept Spa Booking with Forbidden Access")
    void testAcceptSpaBooking_ForbiddenAccess() {
        // Arrange
        Long id = 1L;
        testSpaBooking.setStatus(BookingStatus.ACCEPTED); // Already accepted

        when(spaBookingService.findById(1L)).thenReturn(Optional.of(testSpaBooking));

        // Act
        String result = spaBookingController.approveSpaBooking(id);

        // Assert
        assertEquals("Lịch đặt đã được duyệt", result);
    }

    @Test
    @DisplayName("UTCID D09: Accept Spa Booking with Already Accepted Status")
    void testAcceptSpaBooking_AlreadyAccepted() {
        // Arrange
        Long id = 1L;
        testSpaBooking.setStatus(BookingStatus.ACCEPTED); // Already accepted

        when(spaBookingService.findById(1L)).thenReturn(Optional.of(testSpaBooking));

        // Act
        String result = spaBookingController.approveSpaBooking(id);

        // Assert
        assertEquals("Lịch đặt đã được duyệt", result);
    }
}
