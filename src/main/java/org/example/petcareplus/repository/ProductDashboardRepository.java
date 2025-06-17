package org.example.petcareplus.repository;
import org.example.petcareplus.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductDashboardRepository extends JpaRepository<Product, Integer> {

}
