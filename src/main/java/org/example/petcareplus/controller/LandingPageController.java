package org.example.petcareplus.controller;

import org.example.petcareplus.entity.Category;
import org.example.petcareplus.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class LandingPageController {

    private CategoryService categoryService;

    @Autowired
    public LandingPageController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/")
    public String showLandingPage(Model model) {
        List<Category> parentCategories = categoryService.getParentCategory();
        model.addAttribute("categories", parentCategories);
        return "index";
    }
}
