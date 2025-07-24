package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.Category;
import org.example.petcareplus.repository.CategoryRepository;
import org.example.petcareplus.repository.ProductRepository;
import org.example.petcareplus.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<Category> getParentCategory() {
        List<Category> topCategories = categoryRepository.findByParentIsNull();

        // Lazy fetch có thể cần thiết nếu không tự load sub-categories
        topCategories.forEach(parent -> {
            parent.getSubCategories().size(); // force initialize nếu cần
            parent.getSubCategories().forEach(sub -> sub.getSubCategories().size());
        });

        return topCategories;
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public void saveCategory(String name, Long parentId) {
        Category category = new Category();
        category.setName(name);

        if (parentId != null) {
            Category parent = categoryRepository.findById(parentId).orElse(null);
            category.setParent(parent);
        }

        categoryRepository.save(category);
    }

    @Override
    public boolean deleteCategory(Long categoryId) {
        long productCount = productRepository.countProductsByCategoryId(categoryId);
        if (productCount > 0) {
            return false; // Có sản phẩm, không xóa
        }

        categoryRepository.deleteById(categoryId);
        return true; // Xóa thành công
    }


    @Override
    public Page<Category> getCategoriesPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("categoryId").ascending());
        return categoryRepository.findAll(pageable);
    }

    @Override
    public boolean updateCategory(Long id, String name, Long parentId) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) return false;

        category.setName(name);

        if (parentId != null) {
            Category newParent = categoryRepository.findById(parentId).orElse(null);

            // Kiểm tra nếu parentId là chính nó hoặc con của nó
            if (newParent != null && isChildOf(category, newParent)) {
                return false;
            }

            category.setParent(newParent);
        } else {
            category.setParent(null);
        }

        categoryRepository.save(category);
        return true;
    }

    private boolean isChildOf(Category child, Category potentialParent) {
        if (potentialParent == null || child == null) return false;
        if (potentialParent.getCategoryId().equals(child.getCategoryId())) return true;

        for (Category sub : child.getSubCategories()) {
            if (isChildOf(sub, potentialParent)) return true;
        }
        return false;
    }
}
