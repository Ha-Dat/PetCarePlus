package org.example.petcareplus.service;
import org.example.petcareplus.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ProductDashboardService {
    Page<Product> findAll(Pageable pageable);

    Optional<Product> findById(Integer id);

    Product save(Product product);

    void deleteById(Integer id);

    public Product get(Integer id);

}
