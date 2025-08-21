package org.example.petcareplus.controller;

import org.example.petcareplus.entity.Order;
import org.example.petcareplus.enums.OrderStatus;
import org.example.petcareplus.service.OrderService;
import org.example.petcareplus.service.PaymentService;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderDashboardControllerUpdateOrderStatusTest {

    @Mock
    private OrderService orderService;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private OrderDashboardController orderDashboardController;

    @BeforeEach
    void setUp() {
        // Setup default behavior
    }

    // ========== UPDATEORDERSTATUS() METHOD TEST CASES ==========

    @Test
    @DisplayName("UTCID01: Update with NULL OrderId")
    void testUpdateOrderStatus_NullOrderId() {
        // Arrange
        Long orderId = null;
        String status = "COMPLETED";

        when(orderService.get(null)).thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            orderDashboardController.updateOrderStatus(orderId, status);
        });
        
        verify(orderService).get(null);
        verify(orderService, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("UTCID02: Update with Invalid OrderId Format")
    void testUpdateOrderStatus_InvalidOrderIdFormat() {
        // Arrange
        Long orderId = 1L; // Valid format, but will test with invalid status
        String status = "abc"; // Invalid status format

        Order order = new Order();
        order.setOrderId(orderId);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderItems(new ArrayList<>()); // Initialize empty list to avoid NPE
        when(orderService.get(orderId)).thenReturn(order);

        // Act
        String result = orderDashboardController.updateOrderStatus(orderId, status);

        // Assert
        assertEquals("redirect:/seller/order-dashboard", result);
        verify(orderService).get(orderId);
        verify(orderService, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("UTCID03: Update to Completed Status")
    void testUpdateOrderStatus_ToCompleted() {
        // Arrange
        Long orderId = 1L;
        String status = "COMPLETED";

        Order order = new Order();
        order.setOrderId(orderId);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderItems(new ArrayList<>()); // Initialize empty list to avoid NPE
        when(orderService.get(orderId)).thenReturn(order);

        // Act
        String result = orderDashboardController.updateOrderStatus(orderId, status);

        // Assert
        assertEquals("redirect:/seller/order-dashboard", result);
        verify(orderService).get(orderId);
        verify(orderService).save(any(Order.class));
    }

    @Test
    @DisplayName("UTCID04: Update to Completed Status - ID Not Found")
    void testUpdateOrderStatus_ToCompletedIdNotFound() {
        // Arrange
        Long orderId = 1L;
        String status = "COMPLETED";

        when(orderService.get(orderId)).thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            orderDashboardController.updateOrderStatus(orderId, status);
        });
        
        verify(orderService).get(orderId);
        verify(orderService, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("UTCID05: Update to Processing Status")
    void testUpdateOrderStatus_ToProcessing() {
        // Arrange
        Long orderId = 1L;
        String status = "PROCESSING";

        Order order = new Order();
        order.setOrderId(orderId);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderItems(new ArrayList<>()); // Initialize empty list to avoid NPE
        when(orderService.get(orderId)).thenReturn(order);

        // Act
        String result = orderDashboardController.updateOrderStatus(orderId, status);

        // Assert
        assertEquals("redirect:/seller/order-dashboard", result);
        verify(orderService).get(orderId);
        verify(orderService).save(any(Order.class));
    }

    @Test
    @DisplayName("UTCID06: Update to Processing Status - ID Not Found")
    void testUpdateOrderStatus_ToProcessingIdNotFound() {
        // Arrange
        Long orderId = 1L;
        String status = "PROCESSING";

        when(orderService.get(orderId)).thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            orderDashboardController.updateOrderStatus(orderId, status);
        });
        
        verify(orderService).get(orderId);
        verify(orderService, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("UTCID07: Update to Cancelled Status")
    void testUpdateOrderStatus_ToCancelled() {
        // Arrange
        Long orderId = 1L;
        String status = "CANCELLED";

        Order order = new Order();
        order.setOrderId(orderId);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderItems(new ArrayList<>()); // Initialize empty list to avoid NPE
        when(orderService.get(orderId)).thenReturn(order);

        // Act
        String result = orderDashboardController.updateOrderStatus(orderId, status);

        // Assert
        assertEquals("redirect:/seller/order-dashboard", result);
        verify(orderService).get(orderId);
        verify(orderService).save(any(Order.class));
    }

    @Test
    @DisplayName("UTCID08: Update to Cancelled Status - ID Not Found")
    void testUpdateOrderStatus_ToCancelledIdNotFound() {
        // Arrange
        Long orderId = 1L;
        String status = "CANCELLED";

        when(orderService.get(orderId)).thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            orderDashboardController.updateOrderStatus(orderId, status);
        });
        
        verify(orderService).get(orderId);
        verify(orderService, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("UTCID09: Update to Delivering Status")
    void testUpdateOrderStatus_ToDelivering() {
        // Arrange
        Long orderId = 1L;
        String status = "DELIVERING";

        Order order = new Order();
        order.setOrderId(orderId);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderItems(new ArrayList<>()); // Initialize empty list to avoid NPE
        when(orderService.get(orderId)).thenReturn(order);

        // Act
        String result = orderDashboardController.updateOrderStatus(orderId, status);

        // Assert
        assertEquals("redirect:/seller/order-dashboard", result);
        verify(orderService).get(orderId);
        verify(orderService).save(any(Order.class));
    }

    @Test
    @DisplayName("UTCID10: Update to Delivering Status - ID Not Found")
    void testUpdateOrderStatus_ToDeliveringIdNotFound() {
        // Arrange
        Long orderId = 1L;
        String status = "DELIVERING";

        when(orderService.get(orderId)).thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            orderDashboardController.updateOrderStatus(orderId, status);
        });
        
        verify(orderService).get(orderId);
        verify(orderService, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("UTCID11: Update to Approved Status")
    void testUpdateOrderStatus_ToApproved() {
        // Arrange
        Long orderId = 1L;
        String status = "APPROVED";

        Order order = new Order();
        order.setOrderId(orderId);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderItems(new ArrayList<>()); // Initialize empty list to avoid NPE
        when(orderService.get(orderId)).thenReturn(order);

        // Act
        String result = orderDashboardController.updateOrderStatus(orderId, status);

        // Assert
        assertEquals("redirect:/seller/order-dashboard", result);
        verify(orderService).get(orderId);
        verify(orderService).save(any(Order.class));
    }

    @Test
    @DisplayName("UTCID12: Update to Approved Status - ID Not Found")
    void testUpdateOrderStatus_ToApprovedIdNotFound() {
        // Arrange
        Long orderId = 1L;
        String status = "APPROVED";

        when(orderService.get(orderId)).thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            orderDashboardController.updateOrderStatus(orderId, status);
        });
        
        verify(orderService).get(orderId);
        verify(orderService, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("UTCID13: Update to Rejected Status")
    void testUpdateOrderStatus_ToRejected() {
        // Arrange
        Long orderId = 1L;
        String status = "REJECTED";

        Order order = new Order();
        order.setOrderId(orderId);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderItems(new ArrayList<>()); // Initialize empty list to avoid NPE
        when(orderService.get(orderId)).thenReturn(order);

        // Act
        String result = orderDashboardController.updateOrderStatus(orderId, status);

        // Assert
        assertEquals("redirect:/seller/order-dashboard", result);
        verify(orderService).get(orderId);
        verify(orderService).save(any(Order.class));
    }

    @Test
    @DisplayName("UTCID14: Update to Rejected Status - ID Not Found")
    void testUpdateOrderStatus_ToRejectedIdNotFound() {
        // Arrange
        Long orderId = 1L;
        String status = "REJECTED";

        when(orderService.get(orderId)).thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            orderDashboardController.updateOrderStatus(orderId, status);
        });
        
        verify(orderService).get(orderId);
        verify(orderService, never()).save(any(Order.class));
    }
}
