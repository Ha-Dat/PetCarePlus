package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.example.petcareplus.dto.ChangePasswordDTO;
import org.example.petcareplus.dto.ProfileDTO;
import org.example.petcareplus.entity.*;
import org.example.petcareplus.service.AccountService;
import org.example.petcareplus.service.CategoryService;
import org.example.petcareplus.service.LocationService;
import org.example.petcareplus.service.ProfileService;
import org.example.petcareplus.util.PasswordHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/customer")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private AccountService accountService;

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
        List<City> cities = locationService.getAllCities();
        List<Ward> wards = locationService.getWardsByCityId(profileDTO.getCityId());

        model.addAttribute("cities", cities);
        model.addAttribute("wards", wards);
        model.addAttribute("categories", parentCategories);
        model.addAttribute("profileDTO", profileDTO);
        model.addAttribute("edit", true);
        return "profile";
    }

    @GetMapping("/wards")
    @ResponseBody
    public List<Map<String, Object>> getWardsByCity(@RequestParam Long cityId) {
        return locationService.getWardsByCityId(cityId)
                .stream()
                .map(w -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("wardId", w.getWardId());
                    map.put("name", w.getName());
                    return map;
                })
                .toList();
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

    @GetMapping("/change-password")
    public String showChangePassword(Model model, HttpSession session) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) return "redirect:/login";
        model.addAttribute("changePasswordDTO", new ChangePasswordDTO());
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute("changePasswordDTO") @Valid ChangePasswordDTO changePasswordDTO,
                                 BindingResult bindingResult,
                                 HttpSession session,
                                 Model model) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) return "redirect:/login";

        if (bindingResult.hasErrors()) {
            return "change-password"; // Giữ lại các giá trị đã nhập
        }

        if (!account.getPassword().equals(PasswordHasher.hash(changePasswordDTO.getOldPassword()))) {
            model.addAttribute("error", "Mật khẩu cũ không chính xác.");
            return "change-password";
        }
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            model.addAttribute("error", "Mật khẩu mới và xác nhận mật khẩu không khớp nhau.");
            return "change-password";
        }
        if(changePasswordDTO.getOldPassword().equals(changePasswordDTO.getNewPassword())) {
            model.addAttribute("error", "Mật khẩu mới phải khác mật khẩu cũ.");
            return "change-password";
        }
        account.setPassword(PasswordHasher.hash(changePasswordDTO.getNewPassword()));
        accountService.save(account);
        model.addAttribute("success", "Đổi mật khẩu thành công!");
        return "change-password";
    }
}
