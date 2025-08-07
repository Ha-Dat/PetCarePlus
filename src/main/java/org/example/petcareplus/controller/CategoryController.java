package org.example.petcareplus.controller;

import org.example.petcareplus.entity.Category;
import org.example.petcareplus.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping("/seller")
@Controller
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/category-dashboard")
    public String categoryDashboard(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    Model model) {
        Page<Category> categoryPage = categoryService.getCategoriesPaginated(page, size);
        model.addAttribute("categories", categoryPage.getContent());
        model.addAttribute("allCategories", categoryService.findAll());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        return "category-dashboard";
    }

    @PostMapping("/category/save")
    public String saveCategory(@RequestParam String name,
                               @RequestParam(required = false) Long parentId,
                               RedirectAttributes redirectAttributes) {
        try {
            categoryService.saveCategory(name, parentId);
            redirectAttributes.addFlashAttribute("toastMessage", "Thêm danh mục thành công!");
            redirectAttributes.addFlashAttribute("toastType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("toastMessage", "Thêm danh mục thất bại!");
            redirectAttributes.addFlashAttribute("toastType", "error");
        }
        return "redirect:/seller/category-dashboard";
    }

    @PostMapping("/category/update")
    public String updateCategory(@RequestParam Long categoryId,
                                 @RequestParam String name,
                                 @RequestParam(required = false) Long parentId,
                                 RedirectAttributes redirectAttributes) {
        try {
            boolean updated = categoryService.updateCategory(categoryId, name, parentId);
            if (updated) {
                redirectAttributes.addFlashAttribute("toastMessage", "Cập nhật thành công!");
                redirectAttributes.addFlashAttribute("toastType", "success");
            } else {
                redirectAttributes.addFlashAttribute("toastMessage", "Không thể chọn danh mục con làm danh mục cha!");
                redirectAttributes.addFlashAttribute("toastType", "error");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("toastMessage", "Cập nhật thất bại!");
            redirectAttributes.addFlashAttribute("toastType", "error");
        }
        return "redirect:/seller/category-dashboard";
    }

    @PostMapping("/category/delete")
    public String deleteCategory(@RequestParam Long categoryId, RedirectAttributes redirectAttributes) {
        try {
            boolean deleted = categoryService.deleteCategory(categoryId);
            if (deleted) {
                redirectAttributes.addFlashAttribute("toastMessage", "Xóa danh mục thành công!");
                redirectAttributes.addFlashAttribute("toastType", "success");
            } else {
                redirectAttributes.addFlashAttribute("toastMessage", "Danh mục này đã có sản phẩm, không thể xóa!");
                redirectAttributes.addFlashAttribute("toastType", "error");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("toastMessage", "Lỗi khi xóa danh mục!");
            redirectAttributes.addFlashAttribute("toastType", "error");
        }

        return "redirect:/seller/category-dashboard";
    }
}
