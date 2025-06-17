package org.example.petcareplus.controller;
import org.example.petcareplus.entity.Product;
import org.example.petcareplus.service.ProductDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
@Controller
public class ProductDashboardController {
    private final ProductDashboardService productDashboardService;
    @Autowired
    public ProductDashboardController(final ProductDashboardService productDashboardService) {
        this.productDashboardService = productDashboardService;
    }
    @GetMapping("/productDashboard")
    public String productDashboard(Model model) {
        List<Product> products= productDashboardService.findAll();
        model.addAttribute("products", products);
        return "demo.html";
    }
}
