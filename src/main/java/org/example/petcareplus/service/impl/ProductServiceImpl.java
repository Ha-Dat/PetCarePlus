package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.Product;
import org.example.petcareplus.repository.ProductRepository;
import org.example.petcareplus.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @Override
    public List<Product> getRandomProducts(int count) {
        List<Product> allProducts = productRepository.findAll();
        Collections.shuffle(allProducts);
        return allProducts.stream().limit(count).collect(Collectors.toList());
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getTop4Products() {
        return productRepository.findTop5ByOrderByProductIdAsc();
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
}
