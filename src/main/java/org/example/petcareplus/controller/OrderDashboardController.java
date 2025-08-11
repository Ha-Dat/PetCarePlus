package org.example.petcareplus.controller;

import org.example.petcareplus.dto.OrderDTO;
import org.example.petcareplus.entity.Order;
import org.example.petcareplus.entity.Payment;
import org.example.petcareplus.enums.OrderStatus;
import org.example.petcareplus.repository.PaymentRepository;
import org.example.petcareplus.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping ()
public class OrderDashboardController {

    private final OrderService orderService;
    private final PaymentRepository paymentRepository;

    public OrderDashboardController(OrderService orderService, PaymentRepository paymentRepository) {
        this.orderService = orderService;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/seller/order-dashboard")
    public String orderDashboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            Model model) {

        Page<OrderDTO> orderPage = orderService.filterOrders(orderId, status, startDate, endDate, PageRequest.of(page, size));

        // Tính toán thống kê
        List<OrderDTO> orders = orderPage.getContent();
        long totalOrders = orders.size();
        long completedOrders = orders.stream().filter(o -> o.getStatus() == OrderStatus.COMPLETED).count();
        long pendingOrders = orders.stream().filter(o -> o.getStatus() == OrderStatus.PENDING).count();

        BigDecimal totalRevenue = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED) // lọc đơn hoàn thành
                .map(OrderDTO::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("completedOrders", completedOrders);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("totalRevenue", totalRevenue);

        // Trả lại giá trị filter để giữ nguyên trên giao diện
        model.addAttribute("orderId", orderId);
        model.addAttribute("status", status);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "order-dashboard";
    }

    @GetMapping("/seller/orders/detail/{id}")
    public String getOrderDetail(@PathVariable Long id, Model model) {
        Order order = orderService.get(id);
        Payment payment = paymentRepository.findByOrderOrderId(id);
        model.addAttribute("order", order);
        model.addAttribute("payment", payment);
        return "order-detail";
    }



    @GetMapping("/seller/orders/reject/{id}")
    public String rejectOrder(@PathVariable("id") Long id) {
        Order order = orderService.get(id);
        order.setStatus(OrderStatus.REJECTED);
        orderService.save(order);
        return "redirect:/seller/order-dashboard";
    }

    @GetMapping("/seller/orders/approve/{id}")
    public String approveOrder(@PathVariable("id") Long id) {
        Order order = orderService.get(id);
        order.setStatus(OrderStatus.APPROVED);
        orderService.save(order);
        return "redirect:/seller/order-dashboard";
    }

    @PostMapping("/seller/orders/update-status/{id}")
    public String updateOrderStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
        Order order = orderService.get(id);
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status);
            order.setStatus(orderStatus);
            orderService.save(order);
        } catch (IllegalArgumentException e) {
            // Invalid status, ignore
        }
        return "redirect:/seller/order-dashboard";
    }
}
