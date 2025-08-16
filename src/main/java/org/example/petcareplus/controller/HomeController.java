package org.example.petcareplus.controller;

import org.example.petcareplus.entity.Category;
import org.example.petcareplus.entity.Product;
import org.example.petcareplus.entity.Promotion;
import org.example.petcareplus.enums.ProductStatus;
import org.example.petcareplus.service.CategoryService;
import org.example.petcareplus.service.ProductService;
import org.example.petcareplus.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/")
@Controller
public class HomeController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PromotionService promotionService;

    @GetMapping("/home")
    public String Viewhome(Model model) {
        List<Product> getTop5ProductByCreateDate = productService.getTop5ByOrderByCreatedDateDesc()
                .stream()
                .filter(product -> product.getStatus() != ProductStatus.INACTIVE)
                .toList();
        List<Product> top3BestSellingProducts = productService.getTop3BestSellingProducts()
                .stream()
                .filter(product -> product.getStatus() != ProductStatus.INACTIVE)
                .toList();
        List<Product> AllProduct = productService.getAllProducts()
                .stream()
                .filter(product -> product.getStatus() != ProductStatus.INACTIVE)
                .toList();

        List<Category> parentCategories = categoryService.getParentCategory();

        model.addAttribute("getTop5ProductByCreateDate", getTop5ProductByCreateDate);
        model.addAttribute("top3BestSellingProducts", top3BestSellingProducts);
        model.addAttribute("product", AllProduct);
        model.addAttribute("categories", parentCategories);
        return "home";
    }

    @GetMapping("/about")
    public String aboutShop(Model model) {
        List<Category> parentCategories = categoryService.getParentCategory();
        model.addAttribute("categories", parentCategories);
        return "about";
    }

    @GetMapping("/shipping-policy")
    public String shippingPolicy(Model model) {
        List<Category> parentCategories = categoryService.getParentCategory();
        model.addAttribute("categories", parentCategories);
        return "shipping-policy";
    }

    @GetMapping("/business-license")
    public String businessLicense(Model model) {
        List<Category> parentCategories = categoryService.getParentCategory();
        model.addAttribute("categories", parentCategories);
        return "business-license";
    }

    @GetMapping("/terms-conditions")
    public String termsConditions(Model model) {
        List<Category> parentCategories = categoryService.getParentCategory();
        model.addAttribute("categories", parentCategories);
        return "terms-conditions";
    }

    @GetMapping("/promotions")
    public String promotions(Model model) {
        List<Category> parentCategories = categoryService.getParentCategory();
        
        // Lấy tất cả khuyến mãi từ database
        List<Promotion> allPromotions = promotionService.getAllPromotions();
        
        // Lọc khuyến mãi đang hoạt động và chưa hết hạn
        LocalDateTime now = LocalDateTime.now();
        List<Promotion> activePromotions = allPromotions.stream()
                .filter(promotion -> promotion.getStatus() != null && 
                                   promotion.getStatus().name().equals("ACTIVE") &&
                                   (promotion.getStartDate() == null || promotion.getStartDate().isBefore(now)) &&
                                   (promotion.getEndDate() == null || promotion.getEndDate().isAfter(now)))
                .collect(Collectors.toList());
        
        // Lọc khuyến mãi sắp diễn ra
        List<Promotion> upcomingPromotions = allPromotions.stream()
                .filter(promotion -> promotion.getStatus() != null && 
                                   promotion.getStatus().name().equals("ACTIVE") &&
                                   promotion.getStartDate() != null && 
                                   promotion.getStartDate().isAfter(now))
                .collect(Collectors.toList());
        
        // Lọc khuyến mãi đã hết hạn
        List<Promotion> expiredPromotions = allPromotions.stream()
                .filter(promotion -> promotion.getStatus() != null && 
                                   promotion.getStatus().name().equals("INACTIVE") ||
                                   (promotion.getEndDate() != null && promotion.getEndDate().isBefore(now)))
                .collect(Collectors.toList());
        
        model.addAttribute("categories", parentCategories);
        model.addAttribute("activePromotions", activePromotions);
        model.addAttribute("upcomingPromotions", upcomingPromotions);
        model.addAttribute("expiredPromotions", expiredPromotions);
        model.addAttribute("currentTime", now);
        
        return "promotions";
    }

    @GetMapping("/services")
    public String services(Model model) {
        List<Category> parentCategories = categoryService.getParentCategory();
        model.addAttribute("categories", parentCategories);
        return "services";
    }

    // Helper method để tính số ngày còn lại
    public static long getDaysRemaining(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(from, to);
    }
}
