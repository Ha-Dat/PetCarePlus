package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.entity.Profile;
import org.example.petcareplus.service.OrderService;
import org.example.petcareplus.service.ProductService;
import org.example.petcareplus.service.ProfileService;
import org.example.petcareplus.service.impl.PaymentServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final ProfileService profileService;
    private final ProductService productService;
    private final OrderService orderService;
    private final PaymentServiceImpl paymentService;

    public CheckoutController(ProfileService profileService, ProductService productService, OrderService orderService, PaymentServiceImpl paymentService) {
        this.profileService = profileService;
        this.productService = productService;
        this.orderService = orderService;
        this.paymentService = paymentService;
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

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("subtotal", subtotal);

        return "checkout";
    }

    @PostMapping("/place-order")
    public String placeOrder(
            @RequestParam(required = false) String receiverName,
            @RequestParam(required = false) String receiverPhone,
            @RequestParam(required = false) String deliveryAddress,
            @RequestParam String shippingMethod,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String couponCode,
            @RequestParam(required = false) String note,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("loggedInUser");
        if (userId == null) {
            return "redirect:/login";
        }

        Map<Long, Integer> cartItems = (Map<Long, Integer>) session.getAttribute("cart");
        if (cartItems == null || cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        try {
            // Xử lý đơn hàng
            if ("VNPAY".equals(paymentMethod)) {
                // Tạo URL thanh toán VNPay
                BigDecimal totalAmount = calculateTotalAmount(cartItems, shippingMethod, couponCode);
                String paymentUrl = paymentService.createPaymentUrl(totalAmount, "Thanh toán đơn hàng PetCare+");

                // Lưu thông tin đơn hàng tạm thời trước khi thanh toán
                Long tempOrderId = orderService.saveTempOrder(userId, cartItems, receiverName, receiverPhone,
                        deliveryAddress, shippingMethod, paymentMethod, couponCode, note);

                redirectAttributes.addAttribute("paymentUrl", paymentUrl);
                redirectAttributes.addAttribute("orderId", tempOrderId);
                return "redirect:/checkout/vnpay-redirect";
            } else {
                // Xử lý đơn hàng COD hoặc chuyển khoản
                Long orderId = orderService.processOrder(userId, cartItems, receiverName, receiverPhone,
                        deliveryAddress, shippingMethod, paymentMethod, couponCode, note);

                // Xóa giỏ hàng sau khi đặt hàng thành công
                session.removeAttribute("cart");
                return "redirect:/order-confirmation/" + orderId;
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi đặt hàng: " + e.getMessage());
            return "redirect:/checkout";
        }
    }

    private BigDecimal calculateTotalAmount(Map<Long, Integer> cartItems, String shippingMethod, String couponCode) {
        // Tính toán tổng số tiền (bao gồm phí vận chuyển và giảm giá nếu có)
        BigDecimal subtotal = cartItems.entrySet().stream()
                .map(entry -> {
                    Long productId = entry.getKey();
                    Integer quantity = entry.getValue();
                    return productService.getProductById(productId)
                            .map(product -> product.getPrice().multiply(BigDecimal.valueOf(quantity)))
                            .orElse(BigDecimal.ZERO);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Tính phí vận chuyển (giả sử phí cố định cho ví dụ)
        BigDecimal shippingFee = "EXPRESS".equals(shippingMethod)
                ? new BigDecimal("30000")
                : new BigDecimal("15000");

        // Tính giảm giá (nếu có)
//        BigDecimal discount = orderService.calculateDiscount(couponCode, subtotal);

//        return subtotal.add(shippingFee).subtract(discount);
        return subtotal.add(shippingFee);
    }

}