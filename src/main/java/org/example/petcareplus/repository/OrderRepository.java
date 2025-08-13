package org.example.petcareplus.repository;

import org.example.petcareplus.dto.MonthlyRevenueDTO;
import org.example.petcareplus.entity.Order;
import org.example.petcareplus.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Page<Order> findAll(Pageable pageable);

    @Query("""
    SELECT new org.example.petcareplus.dto.MonthlyRevenueDTO(
        YEAR(o.orderDate), MONTH(o.orderDate), SUM(o.totalPrice)
    )
    FROM Order o
    WHERE o.status = org.example.petcareplus.enums.OrderStatus.COMPLETED
      AND YEAR(o.orderDate) = YEAR(CURRENT_DATE)
    GROUP BY YEAR(o.orderDate), MONTH(o.orderDate)
    ORDER BY MONTH(o.orderDate)
""")
    List<MonthlyRevenueDTO> getMonthlyRevenue();

    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.status = org.example.petcareplus.enums.OrderStatus.COMPLETED")
    BigDecimal getTotalRevenue();

    Page<Order> findByAccount_AccountId(Long accountId, Pageable pageable);

    Page<Order> findByAccount_AccountIdAndStatus(Long accountId, OrderStatus status, Pageable pageable);
}
