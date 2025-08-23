package org.example.petcareplus.controller;

import org.example.petcareplus.entity.Service;
import org.example.petcareplus.enums.ServiceCategory;
import org.example.petcareplus.enums.ServiceStatus;
import org.example.petcareplus.service.ServiceService;
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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ServiceControllerActiveInactiveServiceTest {

    @Mock
    private ServiceService serviceService;

    @InjectMocks
    private ServiceController serviceController;

    private Service testService;

    @BeforeEach
    void setUp() {
        testService = new Service();
        testService.setServiceId(1000L);
        testService.setName("Flea Treatment");
        testService.setServiceCategory(ServiceCategory.SPA);
        testService.setPrice(new BigDecimal("100000"));
        testService.setDuration(30);
        testService.setStatus(ServiceStatus.ACTIVE);
    }

    // ========== BANSERVICE() METHOD TEST CASES ==========

    @Test
    @DisplayName("UTCID D01: Ban Service with NULL Service ID")
    void testBanService_NullServiceId() {
        // Arrange
        Long serviceId = null;

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.banService(serviceId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("Lỗi khi khóa dịch vụ"));
    }

    @Test
    @DisplayName("UTCID D02: Ban Service with Invalid Service ID Format")
    void testBanService_InvalidServiceIdFormat() {
        // Arrange
        Long serviceId = null; // Simulating invalid format

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.banService(serviceId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("Lỗi khi khóa dịch vụ"));
    }

    @Test
    @DisplayName("UTCID D03: Ban Service with Non-existent Service ID")
    void testBanService_NonExistentServiceId() {
        // Arrange
        Long serviceId = 9999L;
        when(serviceService.findById(serviceId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.banService(serviceId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("Không tìm thấy dịch vụ với ID"));
    }

    @Test
    @DisplayName("UTCID D04: Ban Service Successfully")
    void testBanService_Success() {
        // Arrange
        Long serviceId = 1000L;
        when(serviceService.findById(serviceId)).thenReturn(Optional.of(testService));
        when(serviceService.saveService(any(Service.class))).thenReturn(testService);

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.banService(serviceId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("Dịch vụ đã được khóa thành công", responseBody.get("message"));
        
        // Verify service status was changed to BANNED
        verify(serviceService).saveService(argThat(service -> 
            service.getStatus() == ServiceStatus.BANNED
        ));
    }

    @Test
    @DisplayName("UTCID D05: Ban Service with Negative Service ID")
    void testBanService_NegativeServiceId() {
        // Arrange
        Long serviceId = -1L;
        when(serviceService.findById(serviceId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.banService(serviceId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("Không tìm thấy dịch vụ với ID"));
    }

    @Test
    @DisplayName("UTCID D06: Ban Service with Valid Service ID")
    void testBanService_ValidServiceId() {
        // Arrange
        Long serviceId = 1000L;
        when(serviceService.findById(serviceId)).thenReturn(Optional.of(testService));
        when(serviceService.saveService(any(Service.class))).thenReturn(testService);

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.banService(serviceId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("Dịch vụ đã được khóa thành công", responseBody.get("message"));
    }

    @Test
    @DisplayName("UTCID D08: Ban Service with Unauthenticated User")
    void testBanService_UnauthenticatedUser() {
        // Arrange
        Long serviceId = 1000L;
        // No authentication check in controller, so this should work normally
        when(serviceService.findById(serviceId)).thenReturn(Optional.of(testService));
        when(serviceService.saveService(any(Service.class))).thenReturn(testService);

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.banService(serviceId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("Dịch vụ đã được khóa thành công", responseBody.get("message"));
    }

    @Test
    @DisplayName("UTCID D09: Ban Service with Forbidden Access")
    void testBanService_ForbiddenAccess() {
        // Arrange
        Long serviceId = 1000L;
        when(serviceService.findById(serviceId)).thenReturn(Optional.of(testService));
        when(serviceService.saveService(any(Service.class))).thenThrow(new SecurityException("Access denied"));

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.banService(serviceId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("Lỗi khi khóa dịch vụ"));
    }

    @Test
    @DisplayName("UTCID D10: Ban Service with Server Error")
    void testBanService_ServerError() {
        // Arrange
        Long serviceId = 1000L;
        when(serviceService.findById(serviceId)).thenReturn(Optional.of(testService));
        when(serviceService.saveService(any(Service.class))).thenThrow(new RuntimeException("Database connection failed"));

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.banService(serviceId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("Lỗi khi khóa dịch vụ"));
    }

    // ========== UNBANSERVICE() METHOD TEST CASES ==========

    @Test
    @DisplayName("UTCID D11: Unban Service with NULL Service ID")
    void testUnbanService_NullServiceId() {
        // Arrange
        Long serviceId = null;

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.unbanService(serviceId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("Lỗi khi mở khóa dịch vụ"));
    }

    @Test
    @DisplayName("UTCID D12: Unban Service with Invalid Service ID Format")
    void testUnbanService_InvalidServiceIdFormat() {
        // Arrange
        Long serviceId = null; // Simulating invalid format

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.unbanService(serviceId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("Lỗi khi mở khóa dịch vụ"));
    }

    @Test
    @DisplayName("UTCID D13: Unban Service with Non-existent Service ID")
    void testUnbanService_NonExistentServiceId() {
        // Arrange
        Long serviceId = 9999L;
        when(serviceService.findById(serviceId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.unbanService(serviceId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("Không tìm thấy dịch vụ với ID"));
    }

    @Test
    @DisplayName("UTCID D14: Unban Service Successfully")
    void testUnbanService_Success() {
        // Arrange
        Long serviceId = 1000L;
        testService.setStatus(ServiceStatus.BANNED);
        when(serviceService.findById(serviceId)).thenReturn(Optional.of(testService));
        when(serviceService.saveService(any(Service.class))).thenReturn(testService);

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.unbanService(serviceId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("Dịch vụ đã được mở khóa thành công", responseBody.get("message"));
        
        // Verify service status was changed to ACTIVE
        verify(serviceService).saveService(argThat(service -> 
            service.getStatus() == ServiceStatus.ACTIVE
        ));
    }

    @Test
    @DisplayName("UTCID D15: Unban Service with Negative Service ID")
    void testUnbanService_NegativeServiceId() {
        // Arrange
        Long serviceId = -1L;
        when(serviceService.findById(serviceId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.unbanService(serviceId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("Không tìm thấy dịch vụ với ID"));
    }

    @Test
    @DisplayName("UTCID D16: Unban Service with Valid Service ID")
    void testUnbanService_ValidServiceId() {
        // Arrange
        Long serviceId = 1000L;
        testService.setStatus(ServiceStatus.BANNED);
        when(serviceService.findById(serviceId)).thenReturn(Optional.of(testService));
        when(serviceService.saveService(any(Service.class))).thenReturn(testService);

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.unbanService(serviceId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("Dịch vụ đã được mở khóa thành công", responseBody.get("message"));
    }

    @Test
    @DisplayName("UTCID D18: Unban Service with Unauthenticated User")
    void testUnbanService_UnauthenticatedUser() {
        // Arrange
        Long serviceId = 1000L;
        testService.setStatus(ServiceStatus.BANNED);
        // No authentication check in controller, so this should work normally
        when(serviceService.findById(serviceId)).thenReturn(Optional.of(testService));
        when(serviceService.saveService(any(Service.class))).thenReturn(testService);

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.unbanService(serviceId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("Dịch vụ đã được mở khóa thành công", responseBody.get("message"));
    }

    @Test
    @DisplayName("UTCID D19: Unban Service with Forbidden Access")
    void testUnbanService_ForbiddenAccess() {
        // Arrange
        Long serviceId = 1000L;
        testService.setStatus(ServiceStatus.BANNED);
        when(serviceService.findById(serviceId)).thenReturn(Optional.of(testService));
        when(serviceService.saveService(any(Service.class))).thenThrow(new SecurityException("Access denied"));

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.unbanService(serviceId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("Lỗi khi mở khóa dịch vụ"));
    }

    @Test
    @DisplayName("UTCID D20: Unban Service with Server Error")
    void testUnbanService_ServerError() {
        // Arrange
        Long serviceId = 1000L;
        testService.setStatus(ServiceStatus.BANNED);
        when(serviceService.findById(serviceId)).thenReturn(Optional.of(testService));
        when(serviceService.saveService(any(Service.class))).thenThrow(new RuntimeException("Database connection failed"));

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.unbanService(serviceId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("Lỗi khi mở khóa dịch vụ"));
    }
}
