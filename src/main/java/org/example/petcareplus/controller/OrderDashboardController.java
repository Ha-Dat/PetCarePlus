package org.example.petcareplus.controller;

import org.example.petcareplus.dto.OrderDTO;
import org.example.petcareplus.entity.Order;
import org.example.petcareplus.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class OrderDashboardController {

    private final OrderService orderService;

    public OrderDashboardController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orderDashboard")
    public String orderDashboard(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 Model model) {
        Page<OrderDTO> orderPage = orderService.findAllDTO(PageRequest.of(page, size));

        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());

        return "order-dashboard";
    }
    @GetMapping("/orders/reject/{id}")
    public String rejectOrder(@PathVariable("id") Long id) {
        Order order = orderService.get(id);
        order.setStatus("Rejected");
        orderService.save(order);
        return "redirect:/orderDashboard";
    }

}
