package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.example.petcareplus.entity.Profile;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping("profile")
    public String viewProfile(HttpSession session, Model model) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) {
            return "redirect:/login";
        }

        Profile profile = profileService.getProfileByAccountAccountId(account.getAccountId());

        if (profile == null) {
            profile = new Profile();
            profile.setAccount(account);
        }

        model.addAttribute("profile", profile);
        model.addAttribute("cities", profileService.getAllProfiles());
        return "profile"; // file profile.html trong thư mục templates
    }

    @PostMapping("/update")
    public String updateProfile(
            @Valid @ModelAttribute("profile") Profile formProfile,
            BindingResult result,
            HttpSession session,
            Model model
    ) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            model.addAttribute("cities", profileService.getAllCities());
            return "profile";
        }

        // ✅ Lấy profile từ DB hoặc tạo mới nếu chưa có
        Profile existingProfile = profileService.getProfileByAccountAccountId(account.getAccountId());
        if (existingProfile == null) {
            existingProfile = new Profile();
            existingProfile.setAccount(account);
        }

        // ✅ Cập nhật thông tin từ form
        existingProfile.getAccount().setName(formProfile.getAccount().getName());
        existingProfile.getAccount().setPhone(formProfile.getAccount().getPhone());
        existingProfile.setCity(formProfile.getCity());
        existingProfile.setDistrict(formProfile.getDistrict());
        existingProfile.setWard(formProfile.getWard());

        profileService.save(existingProfile);

        return "redirect:/profile";
    }

}
