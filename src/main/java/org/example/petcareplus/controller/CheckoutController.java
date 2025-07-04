package org.example.petcareplus.controller;

import org.example.petcareplus.entity.Product;
import org.example.petcareplus.entity.Profile;
import org.example.petcareplus.service.CheckoutService;
import org.example.petcareplus.service.ProductService;
import org.example.petcareplus.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final ProfileService profileService;
    private final ProductService productService;

    @Autowired
    public CheckoutController(CheckoutService checkoutService,
                              ProfileService profileService, ProductService productService) {
        this.checkoutService = checkoutService;
        this.profileService = profileService;
        this.productService = productService;
    }

    @GetMapping
    public String showCheckoutPage(Model model) {
        // Lấy thông tin profile của người dùng
        int id = 1;
        Profile profile = profileService.getProfileByAccountAccountId(id);
        model.addAttribute("profile", profile);

        // Lấy thông tin giỏ hàng
//        List<CartItem> cartItems = cartService.getCartItems(authentication.getName());
//        BigDecimal subtotal = cartService.calculateSubtotal(cartItems);

        // Tạo dữ liệu giỏ hàng test thay vì lấy từ cartService
//        List<Product> testProducts = productService.getRandomProducts(2); // Lấy 2 sản phẩm ngẫu nhiên
//        List<CartItem> cartItems = testProducts.stream()
//                .map(p -> {
//                    CartItem item = new CartItem();
//                    item.setProduct(p);
//                    item.setQuantity(1); // Mỗi sản phẩm số lượng 1
//                    return item;
//                })
//                .collect(Collectors.toList());
//
//        BigDecimal subtotal = cartItems.stream()
//                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        model.addAttribute("cartItems", cartItems);
//        model.addAttribute("subtotal", subtotal);

        return "checkout";
    }
//
//    @GetMapping("/order-confirmation/{orderId}")
//    public String showOrderConfirmation(@PathVariable Long orderId, Model model) {
//        Order order = checkoutService.getOrderById(orderId);
//        model.addAttribute("order", order);
//        return "order-confirmation";
//    }
}