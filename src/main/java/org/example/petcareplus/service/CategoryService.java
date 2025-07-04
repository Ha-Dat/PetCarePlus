package org.example.petcareplus.service;

import org.example.petcareplus.entity.Category;
import org.example.petcareplus.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CategoryService {
    List<Category> getParentCategory();
}
