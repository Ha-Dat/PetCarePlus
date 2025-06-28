package org.example.petcareplus.controller;

import org.example.petcareplus.entity.Profile;
import org.example.petcareplus.service.CheckoutService;
import org.example.petcareplus.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final ProfileService profileService;

    @Autowired
    public CheckoutController(CheckoutService checkoutService,
                              ProfileService profileService) {
        this.checkoutService = checkoutService;
        this.profileService = profileService;
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