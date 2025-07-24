package org.example.petcareplus.service.impl;

import org.example.petcareplus.dto.OrderDTO;
import org.example.petcareplus.entity.Order;
import org.example.petcareplus.repository.OrderRepository;
import org.example.petcareplus.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepo;

    public OrderServiceImpl(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @Override
    public Page<Order> findAll(Pageable pageable) {
        return orderRepo.findAll(pageable);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderRepo.findById(id);
    }

    @Override
    public Order save(Order order) {
        return orderRepo.save(order);
    }

    @Override
    public void deleteById(Long id) {
        orderRepo.deleteById(id);
    }

    @Override
    public Order get(Long id) {
        Optional<Order> result = orderRepo.findById(id);
        return result.get();
    }

    @Override
    public Page<OrderDTO> findAllDTO(Pageable pageable) {
        return orderRepo.findAll(pageable).map(order ->
                new OrderDTO(
                        order.getOrderId(),
                        order.getAccount() != null ? order.getAccount().getName() : "Unknown",
                        order.getStatus(),
                        order.getTotalPrice(),
                        order.getOrderDate(),
                        order.getPaymentMethod(),
                        order.getDeliverAddress()
                )
        );
    }
}

