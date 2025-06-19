package org.example.petcareplus.controller;

import jakarta.validation.Valid;
import org.example.petcareplus.entity.Profile;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    // Dữ liệu giả lập để demo (chưa kết nối database)
    private Profile profile = new Profile();


    @Autowired
    private CityRepository cityRepository;

    @GetMapping
    public String viewProfile(Model model) {
        // Dữ liệu mặc định
        if (profile.getAccount() == null) {
            Account acc = new Account();
            acc.setName("Nguyễn Văn A");
            acc.setPhone("0901234567");

            profile.setAccount(acc);
            profile.setGender("Nam");
            profile.setDob(LocalDate.of(2000, 1, 1));
            profile.setAvatarPath("/images/default-avatar.png");
        }

        model.addAttribute("profile", profile);
        model.addAttribute("cities", cityRepository.findAll());
        return "profile";
    }


    @PostMapping("/update")
    public String updateProfile(@ModelAttribute("profile") Profile formProfile,
                                @RequestParam("imageFile") MultipartFile imageFile) {

        // Nếu có ảnh mới thì lưu lại
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
            formProfile.setAvatarPath(profile.getAvatarPath()); // giữ nguyên ảnh cũ
        }

        profile = formProfile; // cập nhật lại thông tin hồ sơ
        return "redirect:/profile";
    }
}





