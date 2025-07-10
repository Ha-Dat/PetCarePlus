package org.example.petcareplus.controller;

import org.example.petcareplus.entity.Category;
import org.example.petcareplus.entity.Product;
import org.example.petcareplus.service.CategoryService;
import org.example.petcareplus.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
@RequestMapping("/")
@Controller
public class HomeController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/home")
    public String Viewhome(Model model) {
        List<Product> products = productService.getTop5ByOrderByProductId();
        List<Product> product = productService.getAllProducts();

        List<Category> parentCategories = categoryService.getParentCategory();

        model.addAttribute("products", products);
        model.addAttribute("product", product);
        model.addAttribute("categories", parentCategories);
        return "home";
    }
}
