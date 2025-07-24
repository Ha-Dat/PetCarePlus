package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.example.petcareplus.dto.ProfileDTO;
import org.example.petcareplus.entity.City;
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
    public String viewProfile(@RequestParam(value = "edit", defaultValue = "false") boolean editMode,
                              HttpSession session,
                              Model model) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) return "redirect:/login";

        ProfileDTO profileDTO = profileService.getCurrentProfileByAccountAccountId(account.getAccountId());

        model.addAttribute("profileDTO", profileDTO);
        model.addAttribute("editMode", editMode);
        return "profile";
    }

    @GetMapping("/profile/edit-mode")
    public String switchToEdit(HttpSession session, Model model) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) return "redirect:/login";

        ProfileDTO profileDTO = profileService.getCurrentProfileByAccountAccountId(account.getAccountId());

        model.addAttribute("profileDTO", profileDTO);
        model.addAttribute("editMode", true);
        return "profile";
    }


    @PostMapping("/update")
    public String updateProfile(@ModelAttribute("profileDTO") ProfileDTO profileDTO,
                                HttpSession session) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) return "redirect:/login";

        profileService.updateProfile(profileDTO, account.getAccountId());
        return "redirect:/profile";
    }
}
