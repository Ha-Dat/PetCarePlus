package org.example.petcareplus.controller;
import org.example.petcareplus.dto.ProductDTO;
import org.example.petcareplus.entity.Category;
import org.example.petcareplus.entity.Media;
import org.example.petcareplus.entity.Product;
import org.example.petcareplus.enums.ProductStatus;
import org.example.petcareplus.service.CategoryService;
import org.example.petcareplus.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/seller")
public class ProductDashboardController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private S3Client s3Client;

    @Autowired
    public ProductDashboardController(ProductService productService, CategoryService categoryService, S3Client s3Client) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.s3Client = s3Client;
    }

    @GetMapping("/product-dashboard")
    public String productDashboard(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   Model model) {
        Page<Product> productPage = productService.findAll(PageRequest.of(page, size));
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("productStatus", ProductStatus.values());
        return "product-dashboard";
    }

    @PostMapping("/product-dashboard/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        productService.deleteById(id);
        return "redirect:/seller/product-dashboard";
    }

    @GetMapping(value="/product-dashboard/edit/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Optional<Product> productOptional = productService.findById(id);
        if (productOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Product product = productOptional.get();
        ProductDTO dto = new ProductDTO(
                product.getProductId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getUnitInStock(),
                product.getUnitSold(),
                product.getStatus(),
                product.getMedias(),
                product.getCategory().getCategoryId(),
                product.getCreatedDate()
        );

        return ResponseEntity.ok(dto);
    }

    @ModelAttribute("categoryOptions")
    public List<Category> getCategoryOptions() {
        return categoryService.findAll();
    }

    private Product convertToProduct(ProductDTO dto, Product product) {
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setUnitInStock(dto.getUnitInStock());
        product.setUnitSold(dto.getUnitSold());
        product.setStatus(dto.getStatus());
        product.setCreatedDate(dto.getCreatedDate());

        // Update category
        Category category = categoryService.findById(dto.getCategoryId());
        product.setCategory(category);

        // Update media list
        if (dto.getMedias() != null) {
            List<Media> updatedMedias = new ArrayList<>();

            for (Media media : dto.getMedias()) {
                media.setProduct(product);  // Set the owning side
                updatedMedias.add(media);
            }

            // Ensure product.getMedias() is initialized
            if (product.getMedias() == null) {
                product.setMedias(updatedMedias);
            } else {
                product.getMedias().clear();
                product.getMedias().addAll(updatedMedias);
            }
        }

        return product;
    }

    @PostMapping("/product-dashboard/update")
    @ResponseBody
    public ResponseEntity<?> updateProduct(
            @RequestParam("productId") Long productId,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("unitInStock") int unitInStock,
            @RequestParam(value = "unitSold", defaultValue = "0") int unitSold,
            @RequestParam("status") String status,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        // Lấy product từ DB
        Optional<Product> product = productService.findById(productId);
        if (product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Cập nhật thông tin cơ bản
        product.get().setName(name);
        product.get().setDescription(description);
        product.get().setPrice(price);
        product.get().setUnitInStock(unitInStock);
        product.get().setUnitSold(unitSold);
        product.get().setStatus(ProductStatus.valueOf(status));
        product.get().setCategory(categoryService.findById(categoryId));

        // Nếu có ảnh mới
        if (imageFile != null && !imageFile.isEmpty()) {
            deleteFileFromS3(product.get().getMedias().get(0).getUrl());
            String imageUrl = saveFileToS3(imageFile, "uploads/products/");

            // Nếu product đã có media thì thay ảnh
            if (product.get().getMedias() != null && !product.get().getMedias().isEmpty()) {
                product.get().getMedias().get(0).setUrl(imageUrl);
            } else {
                Media media = new Media();
                media.setUrl(imageUrl);
                media.setProduct(product.orElse(null));
                product.get().setMedias(List.of(media));
            }
        }

        productService.save(product.orElse(null));
        return ResponseEntity.ok().build();
    }



    @PutMapping(value = "/product-dashboard/update/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductDTO dto) {
        try {
            Optional<Product> existing = productService.findById(id);
            if (existing.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Product updatedProduct = convertToProduct(dto, existing.get());
            updatedProduct.setProductId(id);
            productService.save(updatedProduct);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to update product: " + e.getMessage());
        }
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

    private void deleteFileFromS3(String url) {
        String prefix = "https://petcareplus.s3.ap-southeast-2.amazonaws.com/";
        String key = url.replace(prefix, "");

        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket("petcareplus")
                        .key(key)
                        .build()
        );
    }
}