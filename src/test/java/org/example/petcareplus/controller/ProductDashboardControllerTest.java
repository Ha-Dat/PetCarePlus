package org.example.petcareplus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.petcareplus.dto.ProductDTO;
import org.example.petcareplus.entity.Category;
import org.example.petcareplus.entity.Product;
import org.example.petcareplus.service.ProductService;
import org.example.petcareplus.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductDashboardController.class)
class ProductDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product;
    private Category category;

    @BeforeEach
    void setup() {
        category = new Category();
        category.setCategoryId(1L);
        category.setName("Dog");

        product = new Product();
        product.setProductId(1L);
        product.setName("Dog Toy");
        product.setDescription("Durable chew toy");
        product.setPrice(BigDecimal.valueOf(20.99));
        product.setUnitInStock(100);
        product.setUnitSold(10);
        product.setStatus("active");
        product.setImage("dogtoy.jpg");
        product.setCreatedDate(new Date());
        product.setCategory(category);
    }

    @Test
    void testProductDashboard() throws Exception {
        when(productService.findAll(PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(product)));

        mockMvc.perform(get("/product-dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("product-dashboard.html"))
                .andExpect(model().attributeExists("products", "currentPage", "size", "totalPages"));
    }

    @Test
    void testDeleteProduct() throws Exception {
        doNothing().when(productService).deleteById(1L);

        mockMvc.perform(post("/product-dashboard/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/product-dashboard"));
    }

    @Test
    void testGetProductById() throws Exception {
        when(productService.findById(1L)).thenReturn(Optional.of(product));

        mockMvc.perform(get("/product-dashboard/edit/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.name").value("Dog Toy"))
                .andExpect(jsonPath("$.price").value(20.99))
                .andExpect(jsonPath("$.categoryId").value(1));
    }

    @Test
    void testCreateProduct() throws Exception {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productService.save(any(Product.class))).thenReturn(product);

        ProductDTO dto = new ProductDTO(
                null,
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getUnitInStock(),
                product.getUnitSold(),
                product.getStatus(),
                product.getImage(),
                category.getCategoryId(),
                product.getCreatedDate()
        );

        mockMvc.perform(post("/product-dashboard/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateProduct() throws Exception {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productService.save(any(Product.class))).thenReturn(product);

        ProductDTO dto = new ProductDTO(
                1L,
                "Updated Toy",
                product.getDescription(),
                product.getPrice(),
                product.getUnitInStock(),
                product.getUnitSold(),
                product.getStatus(),
                product.getImage(),
                category.getCategoryId(),
                product.getCreatedDate()
        );

        mockMvc.perform(put("/product-dashboard/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}
