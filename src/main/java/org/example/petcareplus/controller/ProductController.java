package org.example.petcareplus.controller;

import org.example.petcareplus.entity.Category;
import org.example.petcareplus.entity.Product;
import org.example.petcareplus.entity.ProductFeedback;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.Order;
import org.example.petcareplus.entity.OrderItem;
import org.example.petcareplus.enums.OrderStatus;
import org.example.petcareplus.enums.ProductStatus;
import org.example.petcareplus.service.CategoryService;
import org.example.petcareplus.service.ProductFeedbackService;
import org.example.petcareplus.service.ProductService;
import org.example.petcareplus.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private ProductFeedbackService productFeedbackService;
    
    @Autowired
    private OrderService orderService;

    @GetMapping("/view-product")
    public String viewAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer priceRange,
            @RequestParam(required = false) String searchKeyword,
            Model model) {

        if (page < 0) page = 0;

        Pageable pageable = PageRequest.of(page, 12);
        BigDecimal minPrice = null, maxPrice = null;

        if (priceRange != null) {
            switch (priceRange) {
                case 1 -> { minPrice = BigDecimal.ZERO; maxPrice = new BigDecimal("100000"); }
                case 2 -> { minPrice = new BigDecimal("100000"); maxPrice = new BigDecimal("500000"); }
                case 3 -> { minPrice = new BigDecimal("500000"); maxPrice = new BigDecimal("1000000"); }
            }
        }

        // Xử lý search kết hợp với filter
        Page<Product> productPage;
        
        // Tạo final variables để sử dụng trong lambda
        final String finalCategory = category;
        final BigDecimal finalMinPrice = minPrice;
        final BigDecimal finalMaxPrice = maxPrice;
        
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            // Nếu có search keyword, tìm theo tên và category, sau đó lọc thông minh
            List<Product> searchResults = productService.findByNameOrCategoryContainingIgnoreCase(searchKeyword.trim())
                    .stream()
                    .filter(product -> product.getStatus() != ProductStatus.INACTIVE)
                    .filter(product -> {
                        // Lọc thông minh: nếu search "chó" thì chỉ hiển thị sản phẩm cho chó
                        String searchTerm = searchKeyword.trim().toLowerCase();
                        String productName = product.getName().toLowerCase();
                        String categoryName = product.getCategory().getName().toLowerCase();
                        
                        // Nếu keyword là "chó" thì chỉ hiển thị sản phẩm có "chó" trong tên hoặc category
                        if (searchTerm.equals("chó")) {
                            return productName.contains("chó") || categoryName.contains("chó");
                        }
                        // Nếu keyword là "mèo" thì chỉ hiển thị sản phẩm có "mèo" trong tên hoặc category
                        else if (searchTerm.equals("mèo")) {
                            return productName.contains("mèo") || categoryName.contains("mèo");
                        }
                        // Với các keyword khác, tìm kiếm bình thường
                        else {
                            return productName.contains(searchTerm) || categoryName.contains(searchTerm);
                        }
                    })
                    .toList();
            
            // Áp dụng filter category và giá cho kết quả search
            List<Product> filteredResults = searchResults.stream()
                    .filter(product -> finalCategory == null || product.getCategory().getName().equals(finalCategory))
                    .filter(product -> {
                        if (finalMinPrice == null && finalMaxPrice == null) return true;
                        if (finalMinPrice != null && product.getPrice().compareTo(finalMinPrice) < 0) return false;
                        if (finalMaxPrice != null && product.getPrice().compareTo(finalMaxPrice) > 0) return false;
                        return true;
                    })
                    .toList();
            
            // Tạo Page object cho pagination
            int start = page * 12;
            int end = Math.min(start + 12, filteredResults.size());
            List<Product> pagedResults = start < filteredResults.size() ? 
                filteredResults.subList(start, end) : new ArrayList<>();
            
            // Tạo Page object
            productPage = new PageImpl<>(pagedResults, PageRequest.of(page, 12), filteredResults.size());
        } else {
            // Nếu không có search keyword, sử dụng filter bình thường
            productPage = productService.searchProducts(keyword, category, minPrice, maxPrice, pageable);
        }
        
        // Lọc bỏ sản phẩm không hoạt động
        List<Product> activeProducts = productPage.getContent().stream()
                .filter(product -> product.getStatus() != ProductStatus.INACTIVE)
                .toList();

        model.addAttribute("products", activeProducts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("categories", categoryService.getParentCategory());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("priceRange", priceRange);
        model.addAttribute("searchkeyword", searchKeyword);

        // Thêm top 3 sản phẩm bán chạy nhất
        List<Product> top3BestSellingProducts = productService.getTop3BestSellingProducts()
                .stream()
                .filter(product -> product.getStatus() != ProductStatus.INACTIVE)
                .limit(3)
                .toList();
        model.addAttribute("top3BestSellingProducts", top3BestSellingProducts);

        // Tạo rating data cho các sản phẩm
        Map<Long, Double> productRatings = new HashMap<>();
        Map<Long, Long> productFeedbackCounts = new HashMap<>();
        
        // Lấy rating cho active products
        for (Product product : activeProducts) {
            Double avgRating = productFeedbackService.getAverageRatingByProductId(product.getProductId());
            long feedbackCount = productFeedbackService.getFeedbackCountByProductId(product.getProductId());
            productRatings.put(product.getProductId(), avgRating != null ? avgRating : 0.0);
            productFeedbackCounts.put(product.getProductId(), feedbackCount);
        }
        

        
        // Lấy rating cho top 3 best selling products
        for (Product product : top3BestSellingProducts) {
            if (!productRatings.containsKey(product.getProductId())) {
                Double avgRating = productFeedbackService.getAverageRatingByProductId(product.getProductId());
                long feedbackCount = productFeedbackService.getFeedbackCountByProductId(product.getProductId());
                productRatings.put(product.getProductId(), avgRating != null ? avgRating : 0.0);
                productFeedbackCounts.put(product.getProductId(), feedbackCount);
            }
        }
        
        model.addAttribute("productRatings", productRatings);
        model.addAttribute("productFeedbackCounts", productFeedbackCounts);

        return "view-product";
    }

    @GetMapping("/product-detail")
    public String showProductDetail(@RequestParam("productId") Long productId, Model model, HttpSession session) {
        Optional<Product> productOptional = productService.findById(productId);
        List<Category> parentCategories = categoryService.getParentCategory();
        List<Product> top9Products = productService.getTop9Products();

        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            
            // Kiểm tra nếu sản phẩm không hoạt động thì ẩn
            if (product.getStatus() == ProductStatus.INACTIVE) {
                return "error/404";
            }
            
            // Lấy feedback data
            List<ProductFeedback> feedbacks = productFeedbackService.getFeedbacksByProductId(productId);
            Double averageRating = productFeedbackService.getAverageRatingByProductId(productId);
            long totalFeedbacks = productFeedbackService.getFeedbackCountByProductId(productId);
            
            // Tính số lượng feedback theo từng rating
            long rating5Count = productFeedbackService.getFeedbackCountByProductIdAndRating(productId, 5);
            long rating4Count = productFeedbackService.getFeedbackCountByProductIdAndRating(productId, 4);
            long rating3Count = productFeedbackService.getFeedbackCountByProductIdAndRating(productId, 3);
            long rating2Count = productFeedbackService.getFeedbackCountByProductIdAndRating(productId, 2);
            long rating1Count = productFeedbackService.getFeedbackCountByProductIdAndRating(productId, 1);
            
            // Kiểm tra quyền feedback của user hiện tại
            Account currentUser = (Account) session.getAttribute("loggedInUser");
            boolean canFeedback = false;
            boolean hasAlreadyFeedback = false;
            int purchaseCount = 0;
            int feedbackCount = 0;
            
            if (currentUser != null) {
                // Đếm số lần user đã mua sản phẩm này
                purchaseCount = getUserPurchaseCount(currentUser.getAccountId(), productId);
                canFeedback = purchaseCount > 0;
                
                // Đếm số lần user đã feedback cho sản phẩm này
                if (canFeedback) {
                    feedbackCount = getUserFeedbackCount(currentUser.getAccountId(), productId);
                    hasAlreadyFeedback = feedbackCount >= purchaseCount;
                }
            }
            
            // Tạo rating data cho top9Products
            Map<Long, Double> productRatings = new HashMap<>();
            Map<Long, Long> productFeedbackCounts = new HashMap<>();
            
            for (Product topProduct : top9Products) {
                Double avgRating = productFeedbackService.getAverageRatingByProductId(topProduct.getProductId());
                long feedbackCount_ProductId = productFeedbackService.getFeedbackCountByProductId(topProduct.getProductId());
                productRatings.put(topProduct.getProductId(), avgRating != null ? avgRating : 0.0);
                productFeedbackCounts.put(topProduct.getProductId(), feedbackCount_ProductId);
            }
            
            model.addAttribute("product", product);
            model.addAttribute("categories", parentCategories);
            model.addAttribute("top9Products", top9Products);
            model.addAttribute("feedbacks", feedbacks);
            model.addAttribute("averageRating", averageRating != null ? averageRating : 0.0);
            model.addAttribute("totalFeedbacks", totalFeedbacks);
            model.addAttribute("rating5Count", rating5Count);
            model.addAttribute("rating4Count", rating4Count);
            model.addAttribute("rating3Count", rating3Count);
            model.addAttribute("rating2Count", rating2Count);
            model.addAttribute("rating1Count", rating1Count);
            model.addAttribute("canFeedback", canFeedback);
            model.addAttribute("hasAlreadyFeedback", hasAlreadyFeedback);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("purchaseCount", purchaseCount);
            model.addAttribute("feedbackCount", feedbackCount);
            model.addAttribute("productRatings", productRatings);
            model.addAttribute("productFeedbackCounts", productFeedbackCounts);
            return "product-detail";
        } else {
            return "error/404";
        }
    }
    
    /**
     * Đếm số lần user đã mua sản phẩm này
     */
    private int getUserPurchaseCount(Long accountId, Long productId) {
        try {
            // Lấy tất cả order của user
            List<Order> userOrders = orderService.findOrdersByAccountId(accountId);
            int count = 0;
            
            for (Order order : userOrders) {
                // Chỉ xét những order đã hoàn thành
                if (order.getStatus() == OrderStatus.COMPLETED) {
                    // Kiểm tra từng item trong order
                    for (OrderItem item : order.getOrderItems()) {
                        if (item.getProduct().getProductId().equals(productId)) {
                            count += item.getQuantity(); // Cộng số lượng mua
                        }
                    }
                }
            }
            return count;
        } catch (Exception e) {
            System.out.println("Error checking purchase count: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Đếm số lần user đã feedback cho sản phẩm này
     */
    private int getUserFeedbackCount(Long accountId, Long productId) {
        try {
            // Kiểm tra xem đã có feedback nào của user này cho sản phẩm này chưa
            List<ProductFeedback> existingFeedbacks = productFeedbackService.findByProductIdAndAccountId(productId, accountId);
            return existingFeedbacks.size();
        } catch (Exception e) {
            System.out.println("Error checking feedback count: " + e.getMessage());
            return 0;
        }
    }
}

