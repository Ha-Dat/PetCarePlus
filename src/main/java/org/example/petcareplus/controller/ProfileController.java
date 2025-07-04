package org.example.petcareplus.controller;

import jakarta.validation.Valid;
import org.example.petcareplus.entity.Profile;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private Profile profile = new Profile();

    @Autowired
    private CityRepository cityRepository;

    @GetMapping
    public String viewProfile(Model model) {
        if (profile.getAccount() == null) {
            Account acc = new Account();
            acc.setName("Nguyễn Văn A");
            acc.setPhone("0912345678");

            profile.setAccount(acc);
            profile.setAvatarPath("/images/default-avatar.png");
        }

        model.addAttribute("profile", profile);
        model.addAttribute("cities", cityRepository.findAll());
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(
            @Valid @ModelAttribute("profile") Profile formProfile,
            BindingResult result,
            @RequestParam("imageFile") MultipartFile imageFile,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("cities", cityRepository.findAll());
            return "profile";
        }

        if (!imageFile.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            File dest = new File("src/main/resources/static/images/" + fileName);
            try {
                imageFile.transferTo(dest);
                formProfile.setAvatarPath("/images/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            formProfile.setAvatarPath(profile.getAvatarPath());
        }

        profile.setAccount(formProfile.getAccount());
        profile.setAvatarPath(formProfile.getAvatarPath());

        profile.setCity(formProfile.getCity());
        profile.setDistrict(formProfile.getDistrict());
        profile.setWard(formProfile.getWard());

        return "redirect:/profile";
    }
}
