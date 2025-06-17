package org.example.petcareplus.service;

import org.example.petcareplus.entity.Product;
import org.example.petcareplus.repository.ProductDashboardRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProductDashboardServiceImpl implements ProductDashboardService {
    private final ProductDashboardRepository productRepo;

    public ProductDashboardServiceImpl(ProductDashboardRepository productRepo) {
        this.productRepo = productRepo;
    }

    @Override
    public List<Product> findAll() {
        return productRepo.findAll();
    }
}
