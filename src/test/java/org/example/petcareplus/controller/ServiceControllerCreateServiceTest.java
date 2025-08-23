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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceControllerCreateServiceTest {

    @Mock
    private ServiceService serviceService;

    @InjectMocks
    private ServiceController serviceController;

    private Service testService;

    @BeforeEach
    void setUp() {
        testService = new Service();
        testService.setServiceId(1L);
        testService.setName("Flea Treatment");
        testService.setServiceCategory(ServiceCategory.SPA);
        testService.setPrice(new BigDecimal("100000"));
        testService.setDuration(30);
        testService.setStatus(ServiceStatus.ACTIVE);
    }

    // ========== CREATESERVICE() METHOD TEST CASES ==========

    @Test
    @DisplayName("UTCID01: Create Service with NULL Service Name")
    void testCreateService_NullServiceName() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("name", null);
        request.put("serviceCategory", "GROOMING");
        request.put("price", 100000);
        request.put("duration", 30);

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.createService(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("Lỗi khi tạo dịch vụ"));
    }

    @Test
    @DisplayName("UTCID02: Create Service Successfully")
    void testCreateService_Success() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Flea Treatment");
        request.put("serviceCategory", "SPA");
        request.put("price", 100000);
        request.put("duration", 30);

        when(serviceService.saveService(any(Service.class))).thenReturn(testService);

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.createService(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("Dịch vụ đã được tạo thành công", responseBody.get("message"));
        assertEquals(1L, responseBody.get("serviceId"));
    }

    @Test
    @DisplayName("UTCID03: Create Service with Non-existent Category")
    void testCreateService_NonExistentCategory() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Test Service");
        request.put("serviceCategory", "NON_EXISTENT_CATEGORY");
        request.put("price", 100000);
        request.put("duration", 30);

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.createService(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("Lỗi khi tạo dịch vụ"));
    }

    @Test
    @DisplayName("UTCID04: Create Service with Valid Category")
    void testCreateService_ValidCategory() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Test Service");
        request.put("serviceCategory", "APPOINTMENT");
        request.put("price", 100000);
        request.put("duration", 30);

        when(serviceService.saveService(any(Service.class))).thenReturn(testService);

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.createService(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("Dịch vụ đã được tạo thành công", responseBody.get("message"));
    }

    @Test
    @DisplayName("UTCID05: Create Service with Negative Price")
    void testCreateService_NegativePrice() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Test Service");
        request.put("serviceCategory", "SPA");
        request.put("price", -1);
        request.put("duration", 30);

        when(serviceService.saveService(any(Service.class))).thenReturn(testService);

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.createService(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("Dịch vụ đã được tạo thành công", responseBody.get("message"));
    }

    @Test
    @DisplayName("UTCID06: Create Service with Invalid Price Format")
    void testCreateService_InvalidPriceFormat() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Test Service");
        request.put("serviceCategory", "SPA");
        request.put("price", "abc");
        request.put("duration", 30);

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.createService(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("Lỗi khi tạo dịch vụ"));
    }

    @Test
    @DisplayName("UTCID07: Create Service with Valid Price")
    void testCreateService_ValidPrice() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Test Service");
        request.put("serviceCategory", "SPA");
        request.put("price", 100000);
        request.put("duration", 30);

        when(serviceService.saveService(any(Service.class))).thenReturn(testService);

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.createService(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("Dịch vụ đã được tạo thành công", responseBody.get("message"));
    }

    @Test
    @DisplayName("UTCID08: Create Service with Negative Duration")
    void testCreateService_NegativeDuration() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Test Service");
        request.put("serviceCategory", "SPA");
        request.put("price", 100000);
        request.put("duration", -1);

        when(serviceService.saveService(any(Service.class))).thenReturn(testService);

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.createService(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("Dịch vụ đã được tạo thành công", responseBody.get("message"));
    }

    @Test
    @DisplayName("UTCID09: Create Service with Invalid Duration Format")
    void testCreateService_InvalidDurationFormat() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Test Service");
        request.put("serviceCategory", "SPA");
        request.put("price", 100000);
        request.put("duration", "abc");

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.createService(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertTrue(responseBody.get("message").toString().contains("Lỗi khi tạo dịch vụ"));
    }

    @Test
    @DisplayName("UTCID10: Create Service with Valid Duration")
    void testCreateService_ValidDuration() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Test Service");
        request.put("serviceCategory", "SPA");
        request.put("price", 100000);
        request.put("duration", 30);

        when(serviceService.saveService(any(Service.class))).thenReturn(testService);

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.createService(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("Dịch vụ đã được tạo thành công", responseBody.get("message"));
    }

    @Test
    @DisplayName("UTCID11: Create Service with Unauthenticated User")
    void testCreateService_UnauthenticatedUser() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Test Service");
        request.put("serviceCategory", "SPA");
        request.put("price", 100000);
        request.put("duration", 30);

        when(serviceService.saveService(any(Service.class))).thenReturn(testService);

        // Act
        ResponseEntity<Map<String, Object>> response = serviceController.createService(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("Dịch vụ đã được tạo thành công", responseBody.get("message"));
    }
}
