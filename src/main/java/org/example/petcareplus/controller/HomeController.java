package org.example.petcareplus.controller;

import org.example.petcareplus.entity.Category;
import org.example.petcareplus.entity.Product;
import org.example.petcareplus.repository.CategoryRepository;
import org.example.petcareplus.repository.ProductRepository;
import org.example.petcareplus.service.CategoryService;
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
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/home")
    public String Viewhome(Model model) {
        List<Product> products = productRepository.findTop4ByOrderByProductIdAsc();
        List<Product> product = productRepository.findAll();

        List<Category> parentCategories = categoryService.getParentCategory();

        model.addAttribute("products", products);
        model.addAttribute("product", product);
        model.addAttribute("categories", parentCategories);
        return "home";
    }
}
