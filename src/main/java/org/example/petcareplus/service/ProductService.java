package org.example.petcareplus.service;

import org.example.petcareplus.entity.Product;
import org.example.petcareplus.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
}
