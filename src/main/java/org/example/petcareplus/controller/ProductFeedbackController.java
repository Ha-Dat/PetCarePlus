package org.example.petcareplus.controller;

import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.Product;
import org.example.petcareplus.entity.ProductFeedback;
import org.example.petcareplus.service.ProductFeedbackService;
import org.example.petcareplus.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/feedback")
public class ProductFeedbackController {

    @Autowired
    private ProductFeedbackService productFeedbackService;

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addFeedback(
            @RequestParam Long productId,
            @RequestParam int rating,
            @RequestParam String comment,
            HttpSession session) {
        
        return createFeedback(productId, rating, comment, session);
    }
    
    private ResponseEntity<Map<String, Object>> createFeedback(
            Long productId,
            int rating,
            String comment,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("=== DEBUG: createFeedback called ===");
            System.out.println("ProductId: " + productId);
            System.out.println("Rating: " + rating);
            System.out.println("Comment: " + comment);
            
            // Lấy thông tin user hiện tại từ session
            Account account = (Account) session.getAttribute("loggedInUser");
            System.out.println("Account from session: " + (account != null ? account.getName() : "NULL"));
            
            if (account == null) {
                response.put("success", false);
                response.put("message", "Bạn phải đăng nhập để đánh giá");
                return ResponseEntity.badRequest().body(response);
            }

            // Tìm product
            var productOpt = productService.findById(productId);
            if (productOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Không tìm thấy sản phẩm");
                return ResponseEntity.badRequest().body(response);
            }

            Product product = productOpt.get();
            
            // Tạo feedback mới
            ProductFeedback feedback = new ProductFeedback();
            feedback.setRating(rating);
            // Nếu comment trống thì để null
            feedback.setComment(comment.trim().isEmpty() ? null : comment.trim());
            feedback.setCreatedAt(LocalDateTime.now());
            feedback.setProduct(product);
            
            // Set account cho feedback
            feedback.setAccount(account);
            
            // Lưu feedback
            System.out.println("Saving feedback...");
            ProductFeedback savedFeedback = productFeedbackService.saveFeedback(feedback);
            System.out.println("Feedback saved with ID: " + savedFeedback.getFeedbackId());
            
            // Tạo DTO để tránh circular reference
            Map<String, Object> feedbackDTO = new HashMap<>();
            feedbackDTO.put("feedbackId", savedFeedback.getFeedbackId());
            feedbackDTO.put("rating", savedFeedback.getRating());
            // Chỉ trả về comment nếu có nội dung
            feedbackDTO.put("comment", savedFeedback.getComment() != null ? savedFeedback.getComment() : "");
            feedbackDTO.put("createdAt", savedFeedback.getCreatedAt());
            
            // Account info
            Map<String, Object> accountDTO = new HashMap<>();
            accountDTO.put("accountId", savedFeedback.getAccount().getAccountId());
            accountDTO.put("name", savedFeedback.getAccount().getName());
            feedbackDTO.put("account", accountDTO);
            
            response.put("success", true);
            response.put("message", "Đánh giá đã được gửi thành công");
            response.put("feedback", feedbackDTO);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.out.println("=== ERROR in createFeedback ===");
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createFeedbackEndpoint(
            @RequestParam Long productId,
            @RequestParam int rating,
            @RequestParam String comment,
            HttpSession session) {
        
        return createFeedback(productId, rating, comment, session);
    }
    
    @GetMapping("/test")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> testEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "ProductFeedbackController is working!");
        return ResponseEntity.ok(response);
    }
}
