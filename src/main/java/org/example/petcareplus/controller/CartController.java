package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.entity.Category;
import org.example.petcareplus.entity.Product;
import org.example.petcareplus.repository.ProductRepository;
import org.example.petcareplus.service.CategoryService;
import org.example.petcareplus.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RequestMapping("/")
@Controller
public class CartController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @PostMapping("/buy-now")
    public String buyNow(@RequestParam("productId") Long productId,
                            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                            HttpSession session,
                            @RequestHeader(value = "Referer", required = false) String referer) {

        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        cart.put(productId, cart.getOrDefault(productId, 0) + quantity);
        session.setAttribute("cart", cart);

        return "redirect:/view-cart";
    }

    @GetMapping("/view-cart")
    public String viewCart(HttpSession session, Model model) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null) cart = new HashMap<>();

        BigDecimal total = BigDecimal.ZERO;
        Map<Product, Integer> productWithQuantity = new LinkedHashMap<>();

        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Product product = productService.getProductById(entry.getKey()).orElse(null);
            if (product != null) {
                productWithQuantity.put(product, entry.getValue());
                total = total.add(product.getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
            }
        }

        List<Category> parentCategories = categoryService.getParentCategory();

        model.addAttribute("cartItems", productWithQuantity);
        model.addAttribute("total", total);
        model.addAttribute("categories", parentCategories);
        return "cart"; // cart.html
    }

    @PostMapping("/update-cart")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCart(@RequestBody Map<String, Object> payload,
                                                          HttpSession session) {
        Long productId = ((Number) payload.get("productId")).longValue();
        Integer quantity = (Integer) payload.get("quantity");

        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        cart.put(productId, quantity);
        session.setAttribute("cart", cart);

        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Product product = productService.getProductById(entry.getKey()).orElse(null);
            if (product != null) {
                total = total.add(product.getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("total", total);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/remove-from-cart")
    @ResponseBody
    public ResponseEntity<?> removeFromCart(@RequestParam("productId") Long productId,
                                            HttpSession session) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart != null) {
            cart.remove(productId);
            session.setAttribute("cart", cart);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/clear-cart")
    @ResponseBody
    public ResponseEntity<?> clearCart(HttpSession session) {
        session.removeAttribute("cart");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cart-total")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCartTotal(HttpSession session) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        BigDecimal total = BigDecimal.ZERO;
        if (cart != null) {
            for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
                Product product = productService.getProductById(entry.getKey()).orElse(null);
                if (product != null) {
                    total = total.add(product.getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
                }
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        return ResponseEntity.ok(result);
    }
}