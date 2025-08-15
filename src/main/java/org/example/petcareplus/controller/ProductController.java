package org.example.petcareplus.controller;

import org.example.petcareplus.entity.Category;
import org.example.petcareplus.entity.Product;
import org.example.petcareplus.entity.ProductFeedback;
import org.example.petcareplus.enums.ProductStatus;
import org.example.petcareplus.service.CategoryService;
import org.example.petcareplus.service.ProductFeedbackService;
import org.example.petcareplus.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

    @GetMapping("/view-product")
    public String viewAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer priceRange,
            @RequestParam(required = false) String searchkeyword,
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

        Page<Product> productPage = productService.searchProducts(keyword, category, minPrice, maxPrice, pageable);
        
        // Lọc bỏ sản phẩm không hoạt động
        List<Product> activeProducts = productPage.getContent().stream()
                .filter(product -> product.getStatus() != ProductStatus.INACTIVE)
                .toList();
        
        List<Product> resultSearch = new ArrayList<>();
        if (searchkeyword != null && !searchkeyword.trim().isEmpty()) {
            resultSearch = productService.findByNameContainingIgnoreCase(searchkeyword.trim())
                    .stream()
                    .filter(product -> product.getStatus() != ProductStatus.INACTIVE)
                    .toList();
        }

        model.addAttribute("products", activeProducts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("categories", categoryService.getParentCategory());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("priceRange", priceRange);
        model.addAttribute("searchkeyword", searchkeyword);
        model.addAttribute("result_product", resultSearch);

        return "view-product";
    }

    @GetMapping("/product-detail")
    public String showProductDetail(@RequestParam("productId") Long productId, Model model) {
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
            return "product-detail";
        } else {
            return "error/404";
        }
    }
}
