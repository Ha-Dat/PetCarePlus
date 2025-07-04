package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.Category;
import org.example.petcareplus.repository.CategoryRepository;
import org.example.petcareplus.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getParentCategory() {
        List<Category> topCategories = categoryRepository.findByParentIsNull();

        // Lazy fetch có thể cần thiết nếu không tự load sub-categories
        topCategories.forEach(parent -> {
            parent.getSubCategories().size(); // force initialize nếu cần
            parent.getSubCategories().forEach(sub -> sub.getSubCategories().size());
        });

        return topCategories;
    }
}
