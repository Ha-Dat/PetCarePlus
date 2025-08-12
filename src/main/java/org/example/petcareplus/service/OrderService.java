package org.example.petcareplus.service;
import org.example.petcareplus.dto.MonthlyRevenueDTO;
import org.example.petcareplus.dto.OrderDTO;
import org.example.petcareplus.entity.Order;
import org.example.petcareplus.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public interface OrderService {
    Page<Order> findAll(Pageable pageable);

    Page<OrderDTO> findAllDTO(Pageable pageable);

    Optional<Order> findById(Long id);

    Order save(Order order);

    void deleteById(Long id);

    public Order get(Long id);

    Long createOrder(Order order, Map<Long, Integer> orderItems);

    Page<OrderDTO> filterOrders(String orderId, String status, LocalDate startDate, LocalDate endDate, Pageable pageable);

    List<MonthlyRevenueDTO> getMonthlyRevenue();

    BigDecimal getTotalRevenue();
}
