package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.Category;
import org.example.petcareplus.entity.Order;
import org.example.petcareplus.enums.OrderStatus;
import org.example.petcareplus.service.CategoryService;
import org.example.petcareplus.service.OrderService;
import org.example.petcareplus.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class OrderController {
    private final OrderService orderService;
    private final CategoryService categoryService;
    private final ProductService productService;

    @Autowired
    public OrderController(final OrderService orderService, CategoryService categoryService, ProductService productService) {
        this.orderService = orderService;
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping("/list-order")
    public String listOrder(HttpSession session,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "5") int size,
                            @RequestParam(required = false) String status,
                            Model model) {

        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) {
            return "redirect:/login";
        }

        List<Category> parentCategories = categoryService.getParentCategory();
        Page<Order> orderPage;

        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());

        // Xử lý lọc theo trạng thái
        if (status != null && !status.isEmpty()) {
            try {
                OrderStatus orderStatus = OrderStatus.valueOf(status);
                orderPage = orderService.findByAccount_AccountIdAndStatus(
                        account.getAccountId(),
                        orderStatus,
                        pageable
                );
            } catch (IllegalArgumentException e) {
                // Nếu status không hợp lệ, lấy tất cả đơn hàng
                orderPage = orderService.findByAccount_AccountId(
                        account.getAccountId(),
                        pageable
                );
            }
        } else {
            orderPage = orderService.findByAccount_AccountId(
                    account.getAccountId(),
                    pageable
            );
        }

        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("categories", parentCategories);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("currentStatus", status); // Thêm trạng thái hiện tại vào model

        return "my-order";
    }

    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable Long orderId,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để thực hiện thao tác này");
            return "redirect:/login";
        }

        try {
            Order order = orderService.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));

            // Kiểm tra xem đơn hàng thuộc về người dùng hiện tại và có trạng thái PENDING không
            if (!order.getAccount().getAccountId().equals(account.getAccountId())) {
                redirectAttributes.addFlashAttribute("error", "Bạn không có quyền hủy đơn hàng này");
                return "redirect:/list-order";
            }

            if (order.getStatus() != OrderStatus.PENDING) {
                redirectAttributes.addFlashAttribute("error", "Chỉ có thể hủy đơn hàng ở trạng thái Chờ duyệt");
                return "redirect:/list-order";
            }

            // Khôi phục số lượng sản phẩm trong kho
            for (var orderItem : order.getOrderItems()) {
                productService.increaseProductQuantity(
                    orderItem.getProduct().getProductId(), 
                    orderItem.getQuantity()
                );
            }

            // Cập nhật trạng thái đơn hàng
            order.setStatus(OrderStatus.CANCELLED);
            orderService.save(order);

            redirectAttributes.addFlashAttribute("success", "Đã hủy đơn hàng thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/list-order";
    }
}
