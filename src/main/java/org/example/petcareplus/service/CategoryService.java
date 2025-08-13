package org.example.petcareplus.service;

import org.example.petcareplus.entity.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {
    List<Category> getParentCategory();

    List<Category> findAll();

    void saveCategory(String name, Long parentId);

    boolean deleteCategory(Long categoryId);

    boolean updateCategory(Long id, String name, Long parentId);

    Category findById(Long id);
}
