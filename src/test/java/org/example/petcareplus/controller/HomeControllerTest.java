package org.example.petcareplus.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import org.example.petcareplus.entity.Product;
import org.example.petcareplus.entity.Category;
import org.example.petcareplus.service.ProductService;
import org.example.petcareplus.service.CategoryService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(HomeController.class)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private CategoryService categoryService;

    @Test
    public void testViewHome() throws Exception {
        // Mock dữ liệu
        List<Product> top5Products = Arrays.asList(new Product(), new Product());
        List<Product> allProducts = Arrays.asList(new Product(), new Product(), new Product());

        // Tạo Category với products không null
        Category cat1 = new Category();
        cat1.setProducts(new ArrayList<>());
        Category cat2 = new Category();
        cat2.setProducts(new ArrayList<>());
        List<Category> parentCategories = Arrays.asList(cat1, cat2);

        // Mock behavior của service
        when(productService.getTop5ByOrderByCreatedDateDesc()).thenReturn(top5Products);
        when(productService.getAllProducts()).thenReturn(allProducts);
        when(categoryService.getParentCategory()).thenReturn(parentCategories);

        // Gửi request và kiểm tra
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("getTop5ProductByCreateDate", top5Products))
                .andExpect(model().attribute("product", allProducts))
                .andExpect(model().attribute("categories", parentCategories));
    }
}