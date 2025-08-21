package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.Category;
import org.example.petcareplus.entity.Product;
import org.example.petcareplus.enums.AccountRole;
import org.example.petcareplus.service.CategoryService;
import org.example.petcareplus.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public ResponseEntity<String> buyNow(@RequestParam("productId") Long productId,
                            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                            HttpSession session,
                            @RequestHeader(value = "Referer", required = false) String referer) {

        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) {
            return ResponseEntity.status(401).body("Bạn cần đăng nhập để mua hàng");
        }

        // Kiểm tra role - chỉ CUSTOMER mới được mua hàng
        if (account.getRole() != AccountRole.CUSTOMER) {
            return ResponseEntity.status(403).body("Chỉ khách hàng mới được phép mua hàng");
        }

        // Kiểm tra sản phẩm có tồn tại không
        Optional<Product> productOpt = productService.getProductById(productId);
        if (productOpt.isEmpty()) {
            return ResponseEntity.status(400).body("Sản phẩm không tồn tại");
        }

        Product product = productOpt.get();
        int unitInStock = product.getUnitInStock();

        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        // Tính tổng số lượng hiện tại trong cart
        int currentQuantityInCart = cart.getOrDefault(productId, 0);
        int newTotalQuantity = currentQuantityInCart + quantity;

        // Kiểm tra xem tổng số lượng có vượt quá stock không
        if (newTotalQuantity > unitInStock) {
            return ResponseEntity.status(400).body("Không đủ hàng trong kho. Số lượng tối đa có thể thêm: " + (unitInStock - currentQuantityInCart));
        }

        cart.put(productId, newTotalQuantity);
        session.setAttribute("cart", cart);

        return ResponseEntity.ok("Đã thêm vào giỏ hàng");
    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<?> addToCart(@RequestParam("productId") Long productId,
                         @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                         HttpSession session,
                         @RequestHeader(value = "Referer", required = false) String referer) {

        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) {
            return ResponseEntity.status(401).body("Bạn cần đăng nhập");
        }

        // Kiểm tra role - chỉ CUSTOMER mới được thêm vào giỏ hàng
        if (account.getRole() != AccountRole.CUSTOMER) {
            return ResponseEntity.status(403).body("Chỉ khách hàng mới được phép thêm vào giỏ hàng");
        }

        // Kiểm tra sản phẩm có tồn tại không
        Optional<Product> productOpt = productService.getProductById(productId);
        if (productOpt.isEmpty()) {
            return ResponseEntity.status(400).body("Sản phẩm không tồn tại");
        }

        Product product = productOpt.get();
        int unitInStock = product.getUnitInStock();

        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        // Tính tổng số lượng hiện tại trong cart
        int currentQuantityInCart = cart.getOrDefault(productId, 0);
        int newTotalQuantity = currentQuantityInCart + quantity;

        // Kiểm tra xem tổng số lượng có vượt quá stock không
        if (newTotalQuantity > unitInStock) {
            return ResponseEntity.status(400).body("Không đủ hàng trong kho. Số lượng tối đa có thể thêm: " + (unitInStock - currentQuantityInCart));
        }

        cart.put(productId, newTotalQuantity);
        session.setAttribute("cart", cart);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/view-cart")
    public String viewCart(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) {
            return "redirect:/login";
        }

        // Kiểm tra role - chỉ CUSTOMER mới được xem giỏ hàng
        if (account.getRole() != AccountRole.CUSTOMER) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chỉ khách hàng mới được phép xem giỏ hàng.");
            return "redirect:/home";
        }

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
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Bạn cần đăng nhập");
            return ResponseEntity.status(401).body(errorResponse);
        }

        // Kiểm tra role - chỉ CUSTOMER mới được cập nhật giỏ hàng
        if (account.getRole() != AccountRole.CUSTOMER) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Chỉ khách hàng mới được phép cập nhật giỏ hàng");
            return ResponseEntity.status(403).body(errorResponse);
        }

        Long productId = ((Number) payload.get("productId")).longValue();
        Integer quantity = (Integer) payload.get("quantity");

        // Kiểm tra sản phẩm có tồn tại không
        Optional<Product> productOpt = productService.getProductById(productId);
        if (productOpt.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Sản phẩm không tồn tại");
            return ResponseEntity.status(400).body(errorResponse);
        }

        Product product = productOpt.get();
        int unitInStock = product.getUnitInStock();

        // Kiểm tra xem số lượng có vượt quá stock không
        if (quantity > unitInStock) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Không đủ hàng trong kho. Số lượng tối đa: " + unitInStock);
            return ResponseEntity.status(400).body(errorResponse);
        }

        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        cart.put(productId, quantity);
        session.setAttribute("cart", cart);

        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Product productInCart = productService.getProductById(entry.getKey()).orElse(null);
            if (productInCart != null) {
                total = total.add(productInCart.getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
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
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) {
            return ResponseEntity.status(401).body("Bạn cần đăng nhập");
        }

        // Kiểm tra role - chỉ CUSTOMER mới được xóa khỏi giỏ hàng
        if (account.getRole() != AccountRole.CUSTOMER) {
            return ResponseEntity.status(403).body("Chỉ khách hàng mới được phép xóa khỏi giỏ hàng");
        }

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
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) {
            return ResponseEntity.status(401).body("Bạn cần đăng nhập");
        }

        // Kiểm tra role - chỉ CUSTOMER mới được xóa giỏ hàng
        if (account.getRole() != AccountRole.CUSTOMER) {
            return ResponseEntity.status(403).body("Chỉ khách hàng mới được phép xóa giỏ hàng");
        }

        session.removeAttribute("cart");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cart-total")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCartTotal(HttpSession session) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Bạn cần đăng nhập");
            return ResponseEntity.status(401).body(errorResponse);
        }

        // Kiểm tra role - chỉ CUSTOMER mới được xem tổng giỏ hàng
        if (account.getRole() != AccountRole.CUSTOMER) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Chỉ khách hàng mới được phép xem tổng giỏ hàng");
            return ResponseEntity.status(403).body(errorResponse);
        }

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