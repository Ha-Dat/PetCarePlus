package org.example.petcareplus.service;
import java.util.Iterator;
import java.util.List;
import org.example.petcareplus.entity.Product;

public interface ProductDashboardService {
    Iterator<Product> findAllProducts();
}
