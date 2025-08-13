package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.dto.CheckoutDTO;
import org.example.petcareplus.entity.*;
import org.example.petcareplus.enums.OrderStatus;
import org.example.petcareplus.enums.PaymentStatus;
import org.example.petcareplus.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private final PaymentService paymentService;

    public CheckoutController(AccountService accountService, ProfileService profileService, ProductService productService, OrderService orderService, PromotionService promotionService, PaymentService paymentService) {
        this.accountService = accountService;
        this.profileService = profileService;
        this.productService = productService;
        this.orderService = orderService;
        this.promotionService = promotionService;
        this.paymentService = paymentService;
    }


    @GetMapping
    public String showCheckoutPage(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // Lấy thông tin profile của người dùng
//        Long id = (Long) session.getAttribute("loggedInUser");
        Account account = (Account) session.getAttribute("loggedInUser");
        Long id = account.getAccountId();

        if (id == null) {
            return "redirect:/login";
        }
        Profile profile = profileService.getProfileByAccountAccountId(id);
        if (profile.getCity() == null || profile.getWard() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn cần cập nhật tỉnh/thành phố và quận/huyện trước khi đặt hàng.");
            return "redirect:/customer/profile";
        }
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
    public String createOrder(HttpSession session, @ModelAttribute CheckoutDTO request, Model model) throws Exception {

        // Check session
//        Long id = (Long) session.getAttribute("loggedInUser");
        Account account = (Account) session.getAttribute("loggedInUser");
        Long id = account.getAccountId();
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
//        Account account = accountService.getById(id).get();

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

        order.setStatus(OrderStatus.PENDING);
        order.setDiscountAmount(request.getDiscountAmount());
        order.setPromotion(promotion);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingMethod(request.getShippingMethod());
        order.setShippingFee(null);

        // Handle Address & Name & Phone
        if (request.isDifferentAddress()) {
            String toAddress = request.getAddress();
            String toWard = request.getWard();
            String toCity = request.getCity();

            order.setDeliverAddress(toAddress + ", " + toWard + ", " + toCity);
            order.setReceiverName(request.getReceiverName());
            order.setReceiverPhone(request.getReceiverPhone());
        } else {
            order.setDeliverAddress(request.getAddress() + ", " + profile.getWard().getName() + ", " + profile.getCity().getName());
            order.setReceiverName(profile.getAccount().getName());
            order.setReceiverPhone(account.getPhone());
        }

        // Create order
        Long orderId =orderService.createOrder(order, cart);

        // If VNPay
        if ("VNPay".equalsIgnoreCase(request.getPaymentMethod())) {
            String vnpayUrl = paymentService.createPaymentUrl(request.getTotalPrice(), orderId);
            return "redirect:" + vnpayUrl;
        }

        model.addAttribute("orderId", orderId);

        // Clear cart
        session.removeAttribute("cart");

        // Redirect to order success page
        return "order-success";
    }

    @GetMapping("/vnpay-return")
    public String vnpayReturn(@RequestParam Map<String, String> params, Model model) {
        // Lưu Payment từ VNPay callback
        Payment savedPayment = paymentService.savePaymentFromVnPayReturn(params);
        Long orderId = savedPayment.getOrder().getOrderId();

        model.addAttribute("orderId", orderId);
        model.addAttribute("payment", savedPayment);
        model.addAttribute("message", savedPayment.getStatus() == PaymentStatus.APPROVED
                ? "Thanh toán thành công!"
                : "Thanh toán thất bại!");

        return "order-success";
    }
}