package org.example.petcareplus.service;

import org.example.petcareplus.entity.Product;

import java.util.List;

public interface ProductService {

    List<Product> getRandomProducts(int count);

}
