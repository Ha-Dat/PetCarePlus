package org.example.petcareplus.controller;

import org.example.petcareplus.dto.ProductDTO;
import org.example.petcareplus.entity.Category;
import org.example.petcareplus.entity.Media;
import org.example.petcareplus.entity.Product;
import org.example.petcareplus.repository.CategoryRepository;
import org.example.petcareplus.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/product-dashboard")
public class ProductDashboardController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    public ProductDashboardController(ProductService productService, CategoryRepository categoryRepository) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String productDashboard(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   Model model) {
        var productPage = productService.findAll(PageRequest.of(page, size));
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", productPage.getTotalPages());
        return "product-dashboard.html";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        productService.deleteById(id);
        return "redirect:/product-dashboard";
    }

    @GetMapping(value = "/edit/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
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
        return categoryRepository.findAll();
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
        Category category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
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

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createProduct(@RequestBody ProductDTO dto) {
        try {
            Product product = convertToProduct(dto, new Product());
            productService.save(product);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to create product: " + e.getMessage());
        }
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
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
}
