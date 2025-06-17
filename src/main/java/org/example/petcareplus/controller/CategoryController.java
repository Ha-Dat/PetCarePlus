package org.example.petcareplus.controller;

import org.springframework.ui.Model;
import org.example.petcareplus.entity.Category;
import org.example.petcareplus.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/")
@Controller
public class CategoryController {
//    private CategoryRepository categoryRepository;
//
//    @Autowired
//    public CategoryController(CategoryRepository categoryRepository) {
//        this.categoryRepository = categoryRepository;
//    }
//
//    @GetMapping("/home")
//    public String ViewCategory(Model model) {
//        List<Category> categories = categoryRepository.findByParentIdIsNull();
//        model.addAttribute("categories", categories);
//        return "home";
//    }

}
