package org.example.petcareplus.controller;

import org.example.petcareplus.entity.Category;
import org.example.petcareplus.entity.Product;
import org.example.petcareplus.repository.ProductRepository;
import org.example.petcareplus.service.CategoryService;
import org.example.petcareplus.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.ArrayList;
import java.util.List;

@RequestMapping("/")
@Controller
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/search")
    public String searchProducts(@RequestParam("keyword") String keyword, Model model) {
        List<Product> searchResults = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            searchResults = productRepository.findByNameContainingIgnoreCase(keyword.trim());
        }
        List<Product> products = productRepository.findTop4ByOrderByProductIdAsc();
        List<Category> parentCategories = categoryService.getParentCategory();

        model.addAttribute("result_product", searchResults);
        model.addAttribute("keyword", keyword);
        model.addAttribute("products", products);
        model.addAttribute("categories", parentCategories);
        return "home"; // tên file HTML kết quả
    }
}
