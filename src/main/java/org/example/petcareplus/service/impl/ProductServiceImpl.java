package org.example.petcareplus.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.example.petcareplus.dto.CategorySalesDTO;
import org.example.petcareplus.entity.Product;
import org.example.petcareplus.repository.ProductRepository;
import org.example.petcareplus.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {this.productRepository = productRepository;}


    @Override
    public List<Product> getRandomProducts(int count) {
        List<Product> allProducts = productRepository.findAll();
        Collections.shuffle(allProducts);
        return allProducts.stream().limit(count).collect(Collectors.toList());
    }

    @Override
    public Page<Product> searchProducts(String keyword, String categoryName, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.searchProducts(keyword, categoryName, minPrice, maxPrice, pageable);
    }

    @Override
    public List<Product> findByNameContainingIgnoreCase(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getTop9Products() {
        return productRepository.findTop9ByOrderByProductIdAsc();
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Product save(Product product) {

        return productRepository.save(product);
    }

    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public Product get(Long id){
        Optional<Product> result = productRepository.findById(id);
        return result.get();
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getTop5ByOrderByCreatedDateDesc() {
        return productRepository.findTop5ByOrderByCreatedDateDesc();
    }

    @Override
    public List<Product> getTop3BestSellingProducts() {
        return productRepository.findTop3ByOrderByUnitSoldDesc();
    }

    @Override
    public List<CategorySalesDTO> getTotalSoldByEachParentCategory() {
        return productRepository.getTotalSoldByEachParentCategory();
    }

    @Override
    public int getTotalUnitsInStock() {
        Integer total = productRepository.getTotalUnitsInStock();
        return total != null ? total : 0;
    }

    @Override
    public int getTotalUnitsSold() {
        Integer total = productRepository.getTotalUnitsSold();
        return total != null ? total : 0;
    }

    @Override
    public void decreaseProductQuantity(Long productId, int quantity) throws InsufficientStockException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        if (product.getUnitInStock() < quantity) {
            throw new InsufficientStockException(
                    "Không đủ hàng trong kho. Sản phẩm: " + product.getName() +
                            ", Số lượng tồn: " + product.getUnitInStock() +
                            ", Số lượng yêu cầu: " + quantity
            );
        }

        product.setUnitInStock(product.getUnitInStock() - quantity);
        productRepository.save(product);
    }

    public class InsufficientStockException extends Exception {
        public InsufficientStockException(String message) {
            super(message);
        }
    }
}
