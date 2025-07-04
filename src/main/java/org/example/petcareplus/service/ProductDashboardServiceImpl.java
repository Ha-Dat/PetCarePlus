package org.example.petcareplus.service;

import org.example.petcareplus.entity.Product;
import org.example.petcareplus.repository.ProductDashboardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ProductDashboardServiceImpl implements ProductDashboardService {
    private final ProductDashboardRepository productRepo;

    public ProductDashboardServiceImpl(ProductDashboardRepository productRepo) {
        this.productRepo = productRepo;
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepo.findAll(pageable);
    }

    @Override
    public Optional<Product> findById(Integer id) {
        return productRepo.findById(id);
    }

    @Override
    public Product save(Product product) {
        return productRepo.save(product);
    }

    @Override
    public void deleteById(Integer id) {
        productRepo.deleteById(id);
    }

    @Override
    public Product get(Integer id){
        Optional<Product> result = productRepo.findById(id);
        return result.get();
    }
}
