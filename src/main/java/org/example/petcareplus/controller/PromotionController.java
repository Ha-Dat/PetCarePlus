package org.example.petcareplus.controller;

import org.example.petcareplus.entity.Promotion;
import org.example.petcareplus.enums.PromotionStatus;
import org.example.petcareplus.service.PromotionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/seller/promotion")
public class PromotionController {

    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping("/list")
    public String getAllPromotions(Model model) {
        List<Promotion> promotions = promotionService.getAllPromotions();
        model.addAttribute("promotions", promotions);
        return "promotion-list";
    }

    @PostMapping("/create")
    public String createPromotion(@ModelAttribute("promotion") Promotion promotion) {
        promotionService.savePromotion(promotion);
        return "redirect:/manager/promotion/list";
    }

    @PostMapping("/change-status/{id}")
    @ResponseBody
    public ResponseEntity<?> changePromotionStatus(@PathVariable("id") Long id) {
        Promotion promotion = promotionService.getPromotionById(id);

        if (promotion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy.");
        }

        PromotionStatus currentStatus = promotion.getStatus();
        PromotionStatus newStatus = (currentStatus == PromotionStatus.ACTIVE)
                ? PromotionStatus.INACTIVE
                : PromotionStatus.ACTIVE;

        promotion.setStatus(newStatus);
        promotionService.updatePromotion(promotion);
        return ResponseEntity.ok("Thay đổi trạng thái thành công.");
    }

    @GetMapping("/details/{id}")
    @ResponseBody
    public ResponseEntity<?> promotionDetails(@PathVariable("id") Long id) {

        Promotion promotion = promotionService.getPromotionById(id);

        if (promotion != null) {
            return ResponseEntity.ok(promotion);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy.");
        }
    }


}
