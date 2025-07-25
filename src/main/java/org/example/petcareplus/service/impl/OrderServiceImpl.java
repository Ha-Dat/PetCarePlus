package org.example.petcareplus.service.impl;

import org.example.petcareplus.dto.OrderDTO;
import org.example.petcareplus.entity.Order;
import org.example.petcareplus.entity.OrderItem;
import org.example.petcareplus.repository.OrderItemRepository;
import org.example.petcareplus.repository.OrderRepository;
import org.example.petcareplus.repository.ProductRepository;
import org.example.petcareplus.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public void deleteById(Long id) {
                orderRepository.deleteById(id);
    }

    @Override
    public Order get(Long id) {
        Optional<Order> result = orderRepository.findById(id);
        return result.get();
    }

    @Override
    public Long createOrder(Order order, Map<Long, Integer> orderItems) {
        // Save order
        orderRepository.save(order);

        // Save order items
        orderItems.forEach((productId, quantity) -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(productRepository.findById(productId).get());
            orderItem.setQuantity(quantity);
            orderItemRepository.save(orderItem);
        });
        return order.getOrderId();
    }

    @Override
    public Page<OrderDTO> findAllDTO(Pageable pageable) {
        return orderRepository.findAll(pageable).map(order ->
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

    @Override
    public Page<OrderDTO> filterOrders(String orderId, String status, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<Order> filtered = orderRepository.findAll((root, query, cb) -> {
            var predicates = cb.conjunction();

            if (orderId != null && !orderId.isBlank()) {
                try {
                    Long parsedId = Long.parseLong(orderId);
                    predicates = cb.and(predicates, cb.equal(root.get("orderId"), parsedId));
                } catch (NumberFormatException ignored) {

                }
            }

            if (status != null && !status.isBlank()) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("status"), status));
            }

            if (startDate != null) {
                predicates = cb.and(predicates,
                        cb.greaterThanOrEqualTo(root.get("orderDate"), startDate.atStartOfDay()));
            }

            if (endDate != null) {
                predicates = cb.and(predicates,
                        cb.lessThanOrEqualTo(root.get("orderDate"), endDate.atTime(23, 59, 59)));
            }

            return predicates;
        }, pageable);

        return filtered.map(order ->
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
