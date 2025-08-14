package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.Category;
import org.example.petcareplus.entity.Order;
import org.example.petcareplus.service.CategoryService;
import org.example.petcareplus.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class OrderController {
    private final OrderService orderService;

    private final CategoryService categoryService;

    @Autowired
    public OrderController(final OrderService orderService, CategoryService categoryService) {
        this.orderService = orderService;
        this.categoryService = categoryService;
    }

    @GetMapping("/customer/list-order")
    public String listOrder(HttpSession session,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "5") int size,
                            Model model) {

        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) {
            return "redirect:/login";
        }

        List<Category> parentCategories = categoryService.getParentCategory();
        Page<Order> orderPage = orderService.findAll(PageRequest.of(page, size));
        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("categories", parentCategories);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        return "my-order";
    }
}
