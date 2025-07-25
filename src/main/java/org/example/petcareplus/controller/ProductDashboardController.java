package org.example.petcareplus.controller;
import org.example.petcareplus.dto.ProductDTO;
import org.example.petcareplus.entity.Category;
import org.example.petcareplus.entity.Product;
import org.example.petcareplus.repository.CategoryRepository;
import org.example.petcareplus.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@Controller
public class ProductDashboardController {
    private final ProductService productService;

    public ProductDashboardController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/productDashboard")
    public String productDashboard(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   Model model) {
        Page<Product> productPage = productService.findAll(PageRequest.of(page, size));
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", productPage.getTotalPages());
        return "product-dashboard.html";
    }

    @PostMapping("/productDashboard/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        productService.deleteById(id);
        return "redirect:/productDashboard";
    }

    @GetMapping(value="/productDashboard/edit/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ProductDTO getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.findById(id);

        return new ProductDTO(
//                product.get().getProductId(),
//                product.get().getName(),
//                product.get().getDescription(),
//                product.get().getPrice(),
//                product.get().getUnitInStock(),
//                product.get().getUnitSold(),
//                product.get().getStatus(),
//                product.get().getImage(),
//                product.get().getCategory().getCategoryId()
        );
    }

    @Autowired
    private CategoryRepository categoryRepository;

    @ModelAttribute("categoryOptions")
    public List<Category> getCategoryOptions() {
        return categoryRepository.findAll();
    }

    private Product convertToProduct(ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setUnitInStock(dto.getUnitInStock());
        product.setUnitSold(dto.getUnitSold());
        product.setStatus(dto.getStatus());
//        product.setImage(dto.getImage());
        Category category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
        product.setCategory(category);

        return product;
    }

    @PostMapping(value = "/productDashboard/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createProduct(@RequestBody ProductDTO dto) {
        Product product = convertToProduct(dto);
        productService.save(product);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/productDashboard/update/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductDTO dto) {
        Product product = convertToProduct(dto);
        product.setProductId(id);
        productService.save(product);
        return ResponseEntity.ok().build();
    }
}
