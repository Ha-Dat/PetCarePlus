package org.example.petcareplus.repository;

import org.example.petcareplus.dto.CategorySalesDTO;
import org.example.petcareplus.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findTop5ByOrderByCreatedDateDesc();
    List<Product> findTop3ByOrderByUnitSoldDesc();
    List<Product> findByNameContainingIgnoreCase(String keyword);
    List<Product> findTop9ByOrderByProductIdAsc();

    @Query("SELECT p FROM Product p " +
            "WHERE (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:categoryName IS NULL OR p.category.name = :categoryName) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> searchProducts(@Param("keyword") String keyword,
                                 @Param("categoryName") String categoryName,
                                 @Param("minPrice") BigDecimal minPrice,
                                 @Param("maxPrice") BigDecimal maxPrice,
                                 Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.categoryId = :categoryId")
    long countProductsByCategoryId(@Param("categoryId") Long categoryId);

    Page<Product> findAll(Pageable pageable);

    @Query("""
        SELECT c.name, SUM(p.unitSold)
        FROM Product p
        JOIN p.category c
        WHERE c.parent IS NULL
        GROUP BY c.name
    """)
    List<CategorySalesDTO> getTotalSoldByEachParentCategory();

    @Query("SELECT SUM(p.unitInStock) FROM Product p WHERE p.status = org.example.petcareplus.enums.ProductStatus.IN_STOCK")
    Integer getTotalUnitsInStock();

    @Query("SELECT SUM(p.unitSold) FROM Product p")
    Integer getTotalUnitsSold();
}
