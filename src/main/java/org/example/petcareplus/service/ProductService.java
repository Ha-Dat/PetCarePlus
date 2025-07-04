package org.example.petcareplus.service;

import org.example.petcareplus.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ProductService {
    List<Product> getRandomProducts(int count);

    List<Product> searchProducts(String keyword);

    Optional<Product> getProductById(Long id);

    List<Product> getTop4Products();

    List<Product> getTop9Products();

    Page<Product> findAll(Pageable pageable);

    Optional<Product> findById(Long id);

    Product save(Product product);

    void deleteById(Long id);

    public Product get(Long id);
}
