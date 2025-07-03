package org.example.petcareplus.service;

import org.example.petcareplus.entity.Product;

import java.util.List;

public interface ProductService {

    List<Product> getRandomProducts(int count);

import org.example.petcareplus.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    public Optional<Product> getProductById(Integer id) {
        return productRepository.findById(id);
    }

    public List<Product> getTop4Products() {
        return productRepository.findTop5ByOrderByProductIdAsc();
    }

    public List<Product> getTop9Products() {
        return productRepository.findTop9ByOrderByProductIdAsc();
    }
}
