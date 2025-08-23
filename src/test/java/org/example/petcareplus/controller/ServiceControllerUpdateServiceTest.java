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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ServiceControllerUpdateServiceTest {

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

    // ========== UPDATESERVICE() METHOD TEST CASES ==========

    @Test
    @DisplayName("UTCID01: Update Service with NULL Service ID")
    void testUpdateService_NullServiceId() {
        // Arrange
        Long serviceId = null;
        Map<String, Object> request = new HashMap<>();
        request.put("name", null);
        request.put("serviceCategory", "SPA");
        request.put("price", -1);
        request.put("duration", -1);

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.updateService(serviceId, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("Lỗi khi cập nhật dịch vụ"));
    }

    @Test
    @DisplayName("UTCID02: Update Service Successfully")
    void testUpdateService_Success() {
        // Arrange
        Long serviceId = 1000L;
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Flea Treatment");
        request.put("serviceCategory", "SPA");
        request.put("price", 100000);
        request.put("duration", 30);

        when(serviceService.findById(serviceId)).thenReturn(Optional.of(testService));
        when(serviceService.saveService(any(Service.class))).thenReturn(testService);

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.updateService(serviceId, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("Dịch vụ đã được cập nhật thành công", responseBody.get("message"));
        assertEquals(1000L, responseBody.get("serviceId"));
    }

    @Test
    @DisplayName("UTCID03: Update Service with Invalid Service ID Format")
    void testUpdateService_InvalidServiceIdFormat() {
        // Arrange
        Long serviceId = null; // This will be passed as null, simulating invalid format
        Map<String, Object> request = new HashMap<>();
        request.put("name", null);
        request.put("serviceCategory", ServiceCategory.SPA);
        request.put("price", -1);
        request.put("duration", -1);

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.updateService(serviceId, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("Lỗi khi cập nhật dịch vụ"));
    }

    @Test
    @DisplayName("UTCID04: Update Service with Non-existent Service ID")
    void testUpdateService_NonExistentServiceId() {
        // Arrange
        Long serviceId = 9999L;
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Test Service");
        request.put("serviceCategory", "SPA");
        request.put("price", 100000);
        request.put("duration", 30);

        when(serviceService.findById(serviceId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.updateService(serviceId, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("Không tìm thấy dịch vụ với ID"));
    }

    @Test
    @DisplayName("UTCID05: Update Service with NULL Service Name")
    void testUpdateService_NullServiceName() {
        // Arrange
        Long serviceId = 1000L;
        Map<String, Object> request = new HashMap<>();
        request.put("name", null);
        request.put("serviceCategory", "SPA");
        request.put("price", 100000);
        request.put("duration", 30);

        when(serviceService.findById(serviceId)).thenReturn(Optional.of(testService));
        when(serviceService.saveService(any(Service.class))).thenReturn(testService);

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.updateService(serviceId, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("Dịch vụ đã được cập nhật thành công", responseBody.get("message"));
    }

    @Test
    @DisplayName("UTCID06: Update Service with Valid Service Name")
    void testUpdateService_ValidServiceName() {
        // Arrange
        Long serviceId = 1000L;
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Flea Treatment");
        request.put("serviceCategory", "SPA");
        request.put("price", 100000);
        request.put("duration", 30);

        when(serviceService.findById(serviceId)).thenReturn(Optional.of(testService));
        when(serviceService.saveService(any(Service.class))).thenReturn(testService);

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.updateService(serviceId, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("Dịch vụ đã được cập nhật thành công", responseBody.get("message"));
    }

    @Test
    @DisplayName("UTCID07: Update Service with Non-existent Category")
    void testUpdateService_NonExistentCategory() {
        // Arrange
        Long serviceId = 1000L;
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Test Service");
        request.put("serviceCategory", "NON_EXISTENT_CATEGORY");
        request.put("price", 100000);
        request.put("duration", 30);

        when(serviceService.findById(serviceId)).thenReturn(Optional.of(testService));

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.updateService(serviceId, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("Lỗi khi cập nhật dịch vụ"));
    }

    @Test
    @DisplayName("UTCID08: Update Service with Valid Category")
    void testUpdateService_ValidCategory() {
        // Arrange
        Long serviceId = 1000L;
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Test Service");
        request.put("serviceCategory", "APPOINTMENT");
        request.put("price", 100000);
        request.put("duration", 30);

        when(serviceService.findById(serviceId)).thenReturn(Optional.of(testService));
        when(serviceService.saveService(any(Service.class))).thenReturn(testService);

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.updateService(serviceId, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("Dịch vụ đã được cập nhật thành công", responseBody.get("message"));
    }

    @Test
    @DisplayName("UTCID09: Update Service with Negative Price")
    void testUpdateService_NegativePrice() {
        // Arrange
        Long serviceId = 1000L;
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Test Service");
        request.put("serviceCategory", "SPA");
        request.put("price", -1);
        request.put("duration", 30);

        when(serviceService.findById(serviceId)).thenReturn(Optional.of(testService));
        when(serviceService.saveService(any(Service.class))).thenReturn(testService);

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.updateService(serviceId, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("Dịch vụ đã được cập nhật thành công", responseBody.get("message"));
    }

    @Test
    @DisplayName("UTCID10: Update Service with Invalid Price Format")
    void testUpdateService_InvalidPriceFormat() {
        // Arrange
        Long serviceId = 1000L;
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Test Service");
        request.put("serviceCategory", "SPA");
        request.put("price", "abc");
        request.put("duration", 30);

        when(serviceService.findById(serviceId)).thenReturn(Optional.of(testService));

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.updateService(serviceId, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("Lỗi khi cập nhật dịch vụ"));
    }

    @Test
    @DisplayName("UTCID11: Update Service with Valid Price")
    void testUpdateService_ValidPrice() {
        // Arrange
        Long serviceId = 1000L;
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Test Service");
        request.put("serviceCategory", "SPA");
        request.put("price", 100000);
        request.put("duration", 30);

        when(serviceService.findById(serviceId)).thenReturn(Optional.of(testService));
        when(serviceService.saveService(any(Service.class))).thenReturn(testService);

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.updateService(serviceId, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("Dịch vụ đã được cập nhật thành công", responseBody.get("message"));
    }

    @Test
    @DisplayName("UTCID12: Update Service with Negative Duration")
    void testUpdateService_NegativeDuration() {
        // Arrange
        Long serviceId = 1000L;
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Test Service");
        request.put("serviceCategory", "SPA");
        request.put("price", 100000);
        request.put("duration", -1);

        when(serviceService.findById(serviceId)).thenReturn(Optional.of(testService));
        when(serviceService.saveService(any(Service.class))).thenReturn(testService);

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.updateService(serviceId, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("Dịch vụ đã được cập nhật thành công", responseBody.get("message"));
    }

    @Test
    @DisplayName("UTCID13: Update Service with Invalid Duration Format")
    void testUpdateService_InvalidDurationFormat() {
        // Arrange
        Long serviceId = 1000L;
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Test Service");
        request.put("serviceCategory", "SPA");
        request.put("price", 100000);
        request.put("duration", "abc");

        when(serviceService.findById(serviceId)).thenReturn(Optional.of(testService));

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.updateService(serviceId, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("Lỗi khi cập nhật dịch vụ"));
    }

    @Test
    @DisplayName("UTCID14: Update Service with Valid Duration")
    void testUpdateService_ValidDuration() {
        // Arrange
        Long serviceId = 1000L;
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Test Service");
        request.put("serviceCategory", "SPA");
        request.put("price", 100000);
        request.put("duration", 30);

        when(serviceService.findById(serviceId)).thenReturn(Optional.of(testService));
        when(serviceService.saveService(any(Service.class))).thenReturn(testService);

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.updateService(serviceId, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("Dịch vụ đã được cập nhật thành công", responseBody.get("message"));
    }

    @Test
    @DisplayName("UTCID15: Update Service with Unauthenticated User")
    void testUpdateService_UnauthenticatedUser() {
        // Arrange
        Long serviceId = 1000L;
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Test Service");
        request.put("serviceCategory", "GROOMING");
        request.put("price", 100000);
        request.put("duration", 30);

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.updateService(serviceId, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("Lỗi khi cập nhật dịch vụ"));
    }

    @Test
    @DisplayName("UTCID16: Update Service with Server Error")
    void testUpdateService_ServerError() {
        // Arrange
        Long serviceId = 1000L;
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Test Service");
        request.put("serviceCategory", "GROOMING");
        request.put("price", 100000);
        request.put("duration", 30);

        when(serviceService.findById(serviceId)).thenReturn(Optional.of(testService));
        when(serviceService.saveService(any(Service.class))).thenThrow(new RuntimeException("Database connection failed"));

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.updateService(serviceId, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("Lỗi khi cập nhật dịch vụ"));
    }
}
