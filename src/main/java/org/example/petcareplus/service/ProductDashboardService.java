package org.example.petcareplus.service;
import org.example.petcareplus.entity.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductDashboardService {
    List<Product> findAll();
}
