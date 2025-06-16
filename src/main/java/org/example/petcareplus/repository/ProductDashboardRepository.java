package org.example.petcareplus.repository;
import org.example.petcareplus.entity.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
public interface ProductDashboardRepository extends CrudRepository<Product, Integer> {

}
