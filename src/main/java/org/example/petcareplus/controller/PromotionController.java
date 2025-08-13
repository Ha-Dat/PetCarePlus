package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.dto.PromotionDTO;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.Media;
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
    public String getAllPromotions(Model model, HttpSession session) {

        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) {
            return "redirect:/login";
        }

        System.out.println(" All promotions: " + promotionService.getAllPromotions());

        List<Promotion> promotions = promotionService.getAllPromotions();
        model.addAttribute("promotions", promotions);
        return "promotion-list";
    }

    @PostMapping("/create")
    public String createPromotion(@ModelAttribute("promotion") Promotion promotion) {
        promotionService.savePromotion(promotion);
        return "redirect:/seller/promotion/list";
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
    public PromotionDTO getPromotionDetails(@PathVariable Long id) {
        Promotion promotion = promotionService.getPromotionById(id);

        System.out.println(" Promotion to get details: " + promotion);

        PromotionDTO dto = new PromotionDTO();
        dto.setPromotionId(promotion.getPromotionId());
        dto.setTitle(promotion.getTitle());
        dto.setDescription(promotion.getDescription());
        dto.setDiscount(promotion.getDiscount());
        dto.setStartDate(promotion.getStartDate());
        dto.setEndDate(promotion.getEndDate());
        dto.setStatus(promotion.getStatus().name());
        dto.setMediaUrls(
                promotion.getMedias()
                        .stream()
                        .map(Media::getUrl)
                        .toList()
        );

        return dto;
    }


}
