package org.example.petcareplus.service;

import org.example.petcareplus.dto.CategorySalesDTO;
import org.example.petcareplus.entity.Product;
import org.example.petcareplus.service.impl.ProductServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public interface ProductService {
    List<Product> getRandomProducts(int count);

    Page<Product> searchProducts(String keyword, String categoryName, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    List<Product> findByNameContainingIgnoreCase(String keyword);
    
    List<Product> findByNameOrCategoryContainingIgnoreCase(String keyword);

    Optional<Product> getProductById(Long id);

    List<Product> getTop5ByOrderByCreatedDateDesc();

    List<Product> getTop9Products();

    Page<Product> findAll(Pageable pageable);

    Optional<Product> findById(Long id);

    Product save(Product product);

    void deleteById(Long id);

    Product get(Long id);

    List<Product> getAllProducts();

    List<CategorySalesDTO> getTotalSoldByEachParentCategory();

    int getTotalUnitsInStock();

    int getTotalUnitsSold();

    void decreaseProductQuantity(Long productId, int quantity) throws ProductServiceImpl.InsufficientStockException;
    
    void increaseProductQuantity(Long productId, int quantity);

    List<Product> getTop3BestSellingProducts();
}
