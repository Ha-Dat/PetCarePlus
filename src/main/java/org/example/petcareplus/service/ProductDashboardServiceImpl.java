package org.example.petcareplus.service;

import org.example.petcareplus.entity.Product;
import org.example.petcareplus.repository.ProductDashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
public class ProductDashboardServiceImpl implements ProductDashboardService {
    @Autowired
    private ProductDashboardRepository productDashboardRepository;

    @Override
    public Iterator<Product> findAllProducts() {
        return productDashboardRepository.findAll().iterator();
    }
}
