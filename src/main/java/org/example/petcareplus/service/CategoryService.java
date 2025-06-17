package org.example.petcareplus.service;

import org.example.petcareplus.entity.Category;
import org.example.petcareplus.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getParentCategory() {
        return categoryRepository.findByParentIsNull();
    }
}
