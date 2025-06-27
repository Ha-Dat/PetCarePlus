package org.example.petcareplus.controller;

import org.example.petcareplus.entity.Order;
import org.example.petcareplus.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OrderController {
    private final OrderService orderService;
    @Autowired
    public OrderController(final OrderService orderService) {
        this.orderService = orderService;
    }
    @GetMapping("/list_order")
    public String listOrder(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   Model model) {
        Page<Order> orderPage = orderService.findAll(PageRequest.of(page, size));
        model.addAttribute("products", orderPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        return "my-order.html";
    }
}
