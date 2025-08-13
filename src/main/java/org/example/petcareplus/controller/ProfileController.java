package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.example.petcareplus.dto.ProfileDTO;
import org.example.petcareplus.entity.City;
import org.example.petcareplus.entity.Category;
import org.example.petcareplus.entity.Profile;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.service.CategoryService;
import org.example.petcareplus.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/customer")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/profile")
    public String viewProfile(@RequestParam(value = "edit", defaultValue = "false") boolean edit,
                              HttpSession session,
                              Model model) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) return "redirect:/login";

        Profile profile = profileService.getProfileByAccountAccountId(account.getAccountId());
        List<Category> parentCategories = categoryService.getParentCategory();
        ProfileDTO profileDTO = profileService.getCurrentProfileByAccountAccountId(account.getAccountId());

        model.addAttribute("profileDTO", profileDTO);
        model.addAttribute("categories", parentCategories);
        model.addAttribute("edit", edit);
        return "profile";
    }

    @GetMapping("/profile/edit")
    public String switchToEdit(HttpSession session, Model model) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) {
            return "redirect:/login";
        }

        ProfileDTO profileDTO = profileService.getCurrentProfileByAccountAccountId(account.getAccountId());
        List<Category> parentCategories = categoryService.getParentCategory();

        model.addAttribute("categories", parentCategories);
        model.addAttribute("profileDTO", profileDTO);
        model.addAttribute("edit", true);
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("profileDTO") ProfileDTO profileDTO,
                                BindingResult bindingResult,
                                HttpSession session,
                                Model model) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) return "redirect:/login";

        if (bindingResult.hasErrors()) {
            model.addAttribute("edit", true);
            model.addAttribute("categories", categoryService.getParentCategory());
            return "profile";
        }
        profileService.updateProfile(profileDTO, account.getAccountId());
        return "redirect:/customer/profile";
    }

}
