package org.example.petcareplus.controller;

import org.example.petcareplus.entity.Category;
import org.example.petcareplus.entity.Product;
import org.example.petcareplus.repository.ProductRepository;
import org.example.petcareplus.service.CategoryService;
import org.example.petcareplus.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class ProductDetailController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @GetMapping("/product-detail")
    public String showProductDetail(@RequestParam("productId") Long productId, Model model) {
        Optional<Product> productOptional = productRepository.findById(productId);
        List<Category> parentCategories = categoryService.getParentCategory();
        List<Product> top9Products = productService.getTop9Products();
        if (productOptional.isPresent()) {
            model.addAttribute("product", productOptional.get());
            model.addAttribute("categories", parentCategories);
            model.addAttribute("top9Products", top9Products);
            return "product-detail";
        } else {
            return "error/404";
        }
    }
}
