package org.example.petcareplus.service;
import org.example.petcareplus.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface OrderService {
    Page<Order> findAll(Pageable pageable);

    Optional<Order> findById(Integer id);

    Order save(Order order);

    void deleteById(Integer id);

    public Order get(Integer id);

}
