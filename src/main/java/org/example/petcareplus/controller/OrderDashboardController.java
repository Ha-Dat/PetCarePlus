package org.example.petcareplus.controller;

import org.example.petcareplus.dto.OrderDTO;
import org.example.petcareplus.entity.Order;
import org.example.petcareplus.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
public class OrderDashboardController {

    private final OrderService orderService;

    public OrderDashboardController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orderDashboard")
    public String orderDashboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            Model model) {

        Page<OrderDTO> orderPage = orderService.filterOrders(orderId, status, startDate, endDate, PageRequest.of(page, size));

        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());

        // Trả lại giá trị filter để giữ nguyên trên giao diện
        model.addAttribute("orderId", orderId);
        model.addAttribute("status", status);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "order-dashboard";
    }

    @GetMapping("/orders/detail/{id}")
    public String getOrderDetail(@PathVariable Long id, Model model) {
        Order order = orderService.get(id);
        model.addAttribute("order", order);
        return "order-detail";
    }



    @GetMapping("/orders/reject/{id}")
    public String rejectOrder(@PathVariable("id") Long id) {
        Order order = orderService.get(id);
        order.setStatus("Rejected");
        orderService.save(order);
        return "redirect:/orderDashboard";
    }

    @GetMapping("/orders/approve/{id}")
    public String approveOrder(@PathVariable("id") Long id) {
        Order order = orderService.get(id);
        order.setStatus("Approved");
        orderService.save(order);
        return "redirect:/orderDashboard";
    }
}
