package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.dto.CheckoutDTO;
import org.example.petcareplus.entity.*;
import org.example.petcareplus.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final AccountService accountService;
    private final ProfileService profileService;
    private final ProductService productService;
    private final OrderService orderService;
    private final PromotionService promotionService;

    public CheckoutController(AccountService accountService, ProfileService profileService, ProductService productService, OrderService orderService, PromotionService promotionService) {
        this.accountService = accountService;
        this.profileService = profileService;
        this.productService = productService;
        this.orderService = orderService;
        this.promotionService = promotionService;
    }

    @GetMapping
    public String showCheckoutPage(Model model, HttpSession session) {
        // Lấy thông tin profile của người dùng
        Long id = (Long) session.getAttribute("loggedInUser");
        if (id == null) {
            return "redirect:/login";
        }
        Profile profile = profileService.getProfileByAccountAccountId(id);
        model.addAttribute("profile", profile);

        // Lấy thông tin giỏ hàng
        Map<Long, Integer> cartItems = (Map<Long, Integer>) session.getAttribute("cart");
        if (cartItems == null || cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        /**
         .map(...): chuyển từng sản phẩm và số lượng thành BigDecimal price * quantity

         .orElse(BigDecimal.ZERO): nếu không tìm thấy sản phẩm thì tính là 0

         .reduce(BigDecimal.ZERO, BigDecimal::add): tính tổng tất cả lại để có subtotal.
         **/
        BigDecimal subtotal = cartItems.entrySet().stream()
                .map(entry -> {
                    Long productId = entry.getKey();
                    Integer quantity = entry.getValue();
                    return productService.getProductById(productId)
                            .map(product -> product.getPrice().multiply(BigDecimal.valueOf(quantity)))
                            .orElse(BigDecimal.ZERO);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<Product, Integer> cartProductItems = new HashMap<>();
        for (Map.Entry<Long, Integer> entry : cartItems.entrySet()) {
            productService.getProductById(entry.getKey()).ifPresent(product -> {
                cartProductItems.put(product, entry.getValue());
            });
        }

        model.addAttribute("cartItems", cartProductItems);
        model.addAttribute("subtotal", subtotal);

        return "checkout";
    }

    @PostMapping("/create")
    public String createOrder(HttpSession session, @ModelAttribute CheckoutDTO request) {

        // Check session
        Long id = (Long) session.getAttribute("loggedInUser");
        if (id == null) {
            return "redirect:/login";
        }

        // Get cart
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart";
        }

        // Get profile
        Profile profile = profileService.getProfileByAccountAccountId(id);

        // Get account
        Account account = accountService.getById(id).get();

        // Handle promotion
        Promotion promotion;
        if (request.getCouponCode() != null) {
            promotion = promotionService.findByTitle(request.getCouponCode());
        } else {
            promotion = null;
        }

        Order order = new Order();

        order.setAccount(account);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setNote(request.getNote());
        order.setTotalPrice(request.getTotalPrice());

        System.out.println(" Check Total Price: " + request.getTotalPrice());

        order.setStatus("PENDING");
        order.setDiscountAmount(request.getDiscountAmount());
        order.setPromotion(promotion);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingMethod(request.getShippingMethod());
        order.setShippingFee(null);

        // Handle Address & Name & Phone
        if (request.isDifferentAddress()) {
            order.setDeliverAddress(request.getDeliveryAddress());
            order.setReceiverName(request.getReceiverName());
            order.setReceiverPhone(request.getReceiverPhone());
        } else {
            order.setDeliverAddress(profile.getWard().getName() + ", " + profile.getDistrict().getName() + ", " + profile.getCity().getName());
            order.setReceiverName(profile.getAccount().getName());
            order.setReceiverPhone(account.getPhone());
        }

        // Create order
        Long orderId =orderService.createOrder(order, cart);

        // Clear cart
        session.removeAttribute("cart");

        // Redirect to order success page

        return "redirect:/checkout/success/" + orderId;
    }

    @GetMapping("/success/{id}")
    public String showOrderSuccessPage(Model model, Long id) {
        model.addAttribute("orderId", id);
        return "order-success";
    }
}