package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.Product;
import org.example.petcareplus.repository.ProductRepository;
import org.example.petcareplus.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
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
}
