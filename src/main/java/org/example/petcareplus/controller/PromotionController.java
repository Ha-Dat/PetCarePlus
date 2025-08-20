package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.dto.PromotionDTO;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.Media;
import org.example.petcareplus.entity.Promotion;
import org.example.petcareplus.enums.MediaCategory;
import org.example.petcareplus.enums.ProductStatus;
import org.example.petcareplus.enums.PromotionStatus;
import org.example.petcareplus.service.PromotionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/seller/promotion")
public class PromotionController {

    private final PromotionService promotionService;
    private final S3Client s3Client;

    public PromotionController(PromotionService promotionService, S3Client s3Client) {
        this.promotionService = promotionService;
        this.s3Client = s3Client;
    }

    @GetMapping("/list")
    public String getAllPromotions(Model model, HttpSession session,
                                   @RequestParam(defaultValue = "0") int page,       // số trang, mặc định 0
                                   @RequestParam(defaultValue = "8") int size        // số phần tử mỗi trang, mặc định 5
    ) {

        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) {
            return "redirect:/login";
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("promotionId").descending());
        Page<Promotion> promotionsPage = promotionService.getAllPromotions(pageable);

        model.addAttribute("promotionsPage", promotionsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        return "promotion-list";
    }


    @PostMapping("/create")
    public String createPromotion(@RequestParam("title") String title,
                                  @RequestParam("description") String description,
                                  @RequestParam("discount") BigDecimal discount,
                                  @RequestParam("startDate") LocalDateTime startDate,
                                  @RequestParam("endDate") LocalDateTime endDate,
                                  @RequestParam("status") PromotionStatus status,
                                  @RequestParam("imageFile") MultipartFile imageFile){
        Promotion promotion = new Promotion();
        promotion.setTitle(title);
        promotion.setDescription(description);
        promotion.setDiscount(discount.divide(BigDecimal.valueOf(100)));
        promotion.setStartDate(startDate);
        promotion.setEndDate(endDate);
        promotion.setStatus(PromotionStatus.valueOf(status.name()));

        if (imageFile != null && !imageFile.isEmpty()) {
            String url = saveFileToS3(imageFile, "upload/promotions/");
            Media media = new Media();
            media.setMediaCategory(MediaCategory.PROMOTION_IMAGE);
            media.setUrl(url);
            media.setPromotion(promotion);
            promotion.setMedias(List.of(media));
        }

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
        Promotion promotion = promotionService.getPromotionWithMedias(id);

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

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<?> updatePromotion(@RequestParam("promotionId") Long id,
                                             @RequestParam("title") String title,
                                             @RequestParam("description") String description,
                                             @RequestParam("discount") BigDecimal discount,
                                             @RequestParam("startDate") LocalDateTime startDate,
                                             @RequestParam("endDate") LocalDateTime endDate,
                                             @RequestParam("status") PromotionStatus status,
                                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

        Promotion promotion = promotionService.getPromotionById(id);
        if (promotion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy khuyến mãi.");
        }

        promotion.setTitle(title);
        promotion.setDescription(description);
        promotion.setDiscount(discount.divide(BigDecimal.valueOf(100)));
        promotion.setStartDate(startDate);
        promotion.setEndDate(endDate);
        promotion.setStatus(status);

        if (imageFile != null && !imageFile.isEmpty()) {
            String url = saveFileToS3(imageFile, "uploads/promotions/");
            Media media = new Media();
            media.setMediaCategory(MediaCategory.PROMOTION_IMAGE);
            media.setUrl(url);
            media.setPromotion(promotion);
            promotion.setMedias(List.of(media));
        }

        promotionService.updatePromotion(promotion);
        return ResponseEntity.ok("Cập nhật thành công");
    }

    //lưu file lên S3
    private String saveFileToS3(MultipartFile file, String folder) {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String key = folder + fileName;
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket("petcareplus")
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    software.amazon.awssdk.core.sync.RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload to S3", e);
        }
        return "https://petcareplus.s3.ap-southeast-2.amazonaws.com/" + key;
    }
}
