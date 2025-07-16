package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.*;
import org.example.petcareplus.repository.*;
import org.example.petcareplus.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;
    private final PaymentRepository paymentRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderServiceImpl(OrderRepository orderRepository, AccountRepository accountRepository, ProductRepository productRepository, PromotionRepository promotionRepository, PaymentRepository paymentRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.accountRepository = accountRepository;
        this.productRepository = productRepository;
        this.promotionRepository = promotionRepository;
        this.paymentRepository = paymentRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public Optional<Order> findById(Integer id) {
        return orderRepository.findById(id);
    }

    @Override
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public void deleteById(Integer id) {
        orderRepository.deleteById(id);
    }

    @Override
    public Order get(Integer id) {
        Optional<Order> result = orderRepository.findById(id);
        return result.get();
    }

    @Override
    @Transactional
    public Long saveTempOrder(Long accountId, Map<Long, Integer> cartItems,
                              String receiverName, String receiverPhone,
                              String deliveryAddress, String shippingMethod,
                              String paymentMethod, String couponCode, String note) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        // Create temporary order (status = PENDING)
        Order order = new Order();
        order.setAccount(account);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setReceiverName(receiverName != null ? receiverName : account.getName());
        order.setReceiverPhone(receiverPhone != null ? receiverPhone : account.getPhone());
        order.setDeliverAddress(deliveryAddress);
        order.setShippingMethod(shippingMethod);
        order.setNote(note);

        // Calculate order details
        calculateOrderDetails(order, cartItems, couponCode);

        // Save payment info
        Payment payment = new Payment();
        payment.setPaymentMethod(paymentMethod);
        payment.setAmount(order.getTotalPrice());
        payment.setStatus("PENDING");
        payment.setOrder(order);

        order.setPaymentMethod(payment.getPaymentMethod());

        Order savedOrder = orderRepository.save(order);
        paymentRepository.save(payment);

        // Save order items
        saveOrderItems(savedOrder, cartItems);

        return savedOrder.getOrderId();
    }

    @Override
    public Long processOrder(Long accountId, Map<Long, Integer> cartItems, String receiverName, String receiverPhone, String deliveryAddress, String shippingMethod, String paymentMethod, String couponCode, String note) {
        // TODO
        return null;
    }

    private void calculateOrderDetails(Order order, Map<Long, Integer> cartItems, String couponCode) {
        // Calculate subtotal
        BigDecimal subtotal = cartItems.entrySet().stream()
                .map(entry -> {
                    Product product = productRepository.findById(entry.getKey())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
                    return product.getPrice().multiply(BigDecimal.valueOf(entry.getValue()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

//        order.setSubtotal(subtotal);

        // Calculate shipping fee (simplified - should call shipping service)
        BigDecimal shippingFee = "EXPRESS".equals(order.getShippingMethod())
                ? new BigDecimal("30000")
                : new BigDecimal("15000");
        order.setShippingFee(shippingFee);

        // Calculate discount
        BigDecimal discount = calculateDiscount(couponCode, subtotal);
        order.setDiscountAmount(discount);

        // Calculate total
        order.setTotalPrice(subtotal.add(shippingFee).subtract(discount));
    }

    public BigDecimal calculateDiscount(String promotionCode, BigDecimal subtotal) {
        if (promotionCode == null || promotionCode.isEmpty()) {
            return BigDecimal.ZERO;
        }

        Promotion promotion = promotionRepository.findByTitle(promotionCode);

        if (!promotion.getStatus().equals("ACTIVE") || promotion.getEndDate().isBefore(LocalDateTime.now())) {
            return BigDecimal.ZERO;
        }

        subtotal.multiply(promotion.getDiscount().divide(BigDecimal.valueOf(100)));

        return promotion.getDiscount().min(subtotal);
    }

    private void saveOrderItems(Order order, Map<Long, Integer> cartItems) {
        List<OrderItem> orderItems = cartItems.entrySet().stream()
                .map(entry -> {
                    Product product = productRepository.findById(entry.getKey())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

                    OrderItem item = new OrderItem();
                    item.setOrder(order);
                    item.setProduct(product);
                    item.setQuantity(entry.getValue());

                    return item;
                })
                .collect(Collectors.toList());

        orderItemRepository.saveAll(orderItems);
        order.setOrderItems(orderItems);
    }

}
