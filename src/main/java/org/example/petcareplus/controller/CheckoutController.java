package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.dto.CheckoutDTO;
import org.example.petcareplus.entity.*;
import org.example.petcareplus.enums.OrderStatus;
import org.example.petcareplus.enums.PaymentStatus;
import org.example.petcareplus.enums.PromotionStatus;
import org.example.petcareplus.service.*;
import org.example.petcareplus.service.impl.ProductServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final ProfileService profileService;
    private final ProductService productService;
    private final OrderService orderService;
    private final PromotionService promotionService;
    private final PaymentService paymentService;

    public CheckoutController(ProfileService profileService, ProductService productService, OrderService orderService, PromotionService promotionService, PaymentService paymentService) {
        this.profileService = profileService;
        this.productService = productService;
        this.orderService = orderService;
        this.promotionService = promotionService;
        this.paymentService = paymentService;
    }


    @GetMapping
    public String showCheckoutPage(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // Lấy thông tin profile của người dùng
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

        try {
            // Kiểm tra và trừ số lượng tồn kho trước khi tạo đơn hàng
            for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
                productService.decreaseProductQuantity(entry.getKey(), entry.getValue());
            }

        // Get profile
        Profile profile = profileService.getProfileByAccountAccountId(id);

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

        // Handle Address & Name & Phone
        if (request.isDifferentAddress()) {
            String toAddress = cleanInput(request.getAddress());
            String toWard = cleanInput(request.getWard());
            String toCity = cleanInput(request.getCity());

            order.setDeliverAddress(toAddress + ", " + toWard + ", " + toCity);
            order.setReceiverName(request.getReceiverName());
            order.setReceiverPhone(request.getReceiverPhone());
            order.setShippingFee(calculateShippingFee(toCity));
        } else {
            order.setDeliverAddress(request.getAddress() + " " + profile.getWard().getName() + ", " + profile.getCity().getName());
            order.setReceiverName(profile.getAccount().getName());
            order.setReceiverPhone(account.getPhone());
            order.setShippingFee(calculateShippingFee(profile.getCity().getName()));
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
        } catch (ProductServiceImpl.InsufficientStockException e) {
            // Xử lý khi không đủ hàng
            return "redirect:/cart?error=" + URLEncoder.encode(e.getMessage(), "UTF-8");
        } catch (Exception e) {
            // Xử lý các lỗi khác
            return "redirect:/cart?error=Có lỗi xảy ra khi tạo đơn hàng";
        }
    }

    @GetMapping("/vnpay-return")
    public String vnpayReturn(@RequestParam Map<String, String> params, Model model) {
        // Lưu Payment từ VNPay callback
        Payment savedPayment = paymentService.savePaymentFromVnPayReturn(params);
        Long orderId = savedPayment.getOrder().getOrderId();

        // Update order status
        orderService.updateStatus(orderId, OrderStatus.PROCESSING);

        model.addAttribute("orderId", orderId);
        model.addAttribute("payment", savedPayment);
        model.addAttribute("message", savedPayment.getStatus() == PaymentStatus.APPROVED
                ? "Thanh toán thành công!"
                : "Thanh toán thất bại!");

        return "order-success";
    }

    @PostMapping("/apply-coupon")
    @ResponseBody
    public Map<String, Object> applyCoupon(@RequestBody Map<String, Object> payload) {
        Map<String, Object> res = new HashMap<>();

        String couponCode = payload.get("couponCode") != null
                ? payload.get("couponCode").toString().trim()
                : "";

        // Validation backend
        if (couponCode.isEmpty()) {
            res.put("valid", false);
            res.put("message", "Mã giảm giá không được để trống.");
            return res;
        }
        if (couponCode.contains(" ")) {
            res.put("valid", false);
            res.put("message", "Mã giảm giá không được chứa khoảng trắng.");
            return res;
        }

        BigDecimal subtotal;
        try {
            subtotal = new BigDecimal(payload.get("subtotal").toString());
        } catch (Exception e) {
            res.put("valid", false);
            res.put("message", "Giá trị đơn hàng không hợp lệ.");
            return res;
        }

        Promotion promo = promotionService.findByTitle(couponCode);
        if (promo == null) {
            res.put("valid", false);
            res.put("message", "Mã giảm giá không tồn tại.");
            return res;
        }

        LocalDateTime now = LocalDateTime.now();
        if (promo.getStartDate().isAfter(now) || promo.getEndDate().isBefore(now) || promo.getStatus() != PromotionStatus.ACTIVE) {
            res.put("valid", false);
            res.put("message", "Mã giảm giá đã hết hạn hoặc chưa bắt đầu.");
            return res;
        }

        // Tính giảm giá
        BigDecimal discountPercent = promo.getDiscount();
        BigDecimal discountAmount = subtotal.multiply(discountPercent);
        BigDecimal total = subtotal.subtract(discountAmount);

        res.put("valid", true);
        res.put("discount", discountAmount);
        res.put("total", total);
        res.put("discountFormatted", String.format("%,.0f VND", discountAmount));
        res.put("totalFormatted", String.format("%,.0f VND", total));
        return res;
    }

    private BigDecimal calculateShippingFee(String city) {
        if ("Ha Noi".equalsIgnoreCase(city) || "Hà Nội".equalsIgnoreCase(city)) {
            return BigDecimal.valueOf(15000);
        }
        return BigDecimal.valueOf(30000);
    }

    private String cleanInput(String input) {
        if (input == null) return "";
        return input
                .trim() // bỏ khoảng trắng ở đầu và cuối
                .replaceAll("^,+", "") // bỏ dấu phẩy ở đầu
                .replaceAll(",+$", "") // bỏ dấu phẩy ở cuối
                .trim()
                .replaceAll("\\s+", " "); // gộp nhiều khoảng trắng thành 1
    }
}