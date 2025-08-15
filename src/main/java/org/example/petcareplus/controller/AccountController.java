package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.example.petcareplus.dto.LoginDTO;
import org.example.petcareplus.dto.RegisterDTO;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.Profile;
import org.example.petcareplus.enums.AccountRole;
import org.example.petcareplus.enums.AccountStatus;
import org.example.petcareplus.service.AccountService;
import org.example.petcareplus.util.PasswordHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class    AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        if (!model.containsAttribute("loginDTO")) {
            model.addAttribute("loginDTO", new LoginDTO());
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        return "register";
    }
    
    @PostMapping("/register")
    public String register(@ModelAttribute("registerDTO") @Valid RegisterDTO registerDTO,
                           RedirectAttributes redirectAttributes,
                           BindingResult result,
                           HttpSession session,
                           Model model) {

        if (result.hasErrors()) {
            return "register";
        }

        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp!");
            return "register";
        }

        if (accountService.isPhoneExists(registerDTO.getPhone())) {
            model.addAttribute("error", "Số điện thoại đã tồn tại!");
            return "register";
        }

        // Tạo tài khoản với trạng thái ACTIVE (không cần OTP verification)
        Account account = new Account();
        account.setName(registerDTO.getName());
        account.setPhone(registerDTO.getPhone());
        account.setPassword(PasswordHasher.hash(registerDTO.getPassword()));
        account.setRole(AccountRole.CUSTOMER);
        account.setStatus(AccountStatus.ACTIVE); // Kích hoạt ngay lập tức

        accountService.save(account);

        // Tạo hồ sơ người dùng nếu chưa có
        Profile existingProfile = accountService.getProfileByAccountAccountId(account.getAccountId());
        if (existingProfile == null) {
            existingProfile = new Profile();
            existingProfile.setAccount(account);
        }
        accountService.profileSave(existingProfile);

        // Chuyển về trang login với thông báo thành công
        redirectAttributes.addFlashAttribute("message", "Đăng ký thành công! Hãy đăng nhập.");
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("loginDTO") @Valid LoginDTO loginDTO,
                        BindingResult result,
                        HttpSession session,
                        Model model) {
        if (result.hasErrors()) {
            return "login";
        }

        Optional<Account> accountOpt = accountService.login(loginDTO.getPhone(), loginDTO.getPassword());

        if (accountOpt.isEmpty()) {
            model.addAttribute("error", "Sai tài khoản hoặc mật khẩu");
            return "login";
        }

        Account account = accountOpt.get();

        if (account.getStatus() == AccountStatus.BANNED) {
            model.addAttribute("error", "Tài khoản bị khóa!");
            return "login";
        }

        Long id = account.getAccountId();

// Gán quyền thủ công vào SecurityContext
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        account.getPhone(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + account.getRole()))
                );

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        // ✅ Gán vào session để Spring Security giữ context giữa các request
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

// Gán vào session (tuỳ, nếu bạn cần dùng account trong session riêng)
        session.setAttribute("loggedInUser", account);

        // Điều hướng dựa theo vai trò
        switch (account.getRole()) {
            case ADMIN -> {
                return "redirect:/admin/dashboard";
            }
            case SELLER -> {
                return "redirect:/seller/dashboard";
            }
            case VET -> {
                return "redirect:vet/appointment/pending";
            }
            case PET_GROOMER -> {
                return "redirect:/pet-groomer/dashboard";
            }
            case MANAGER -> {
                return "redirect:/manager/service-dashboard";
            }
            case CUSTOMER -> {
                return "redirect:/home";
            }
            default -> {
                return "redirect:/home";
            }
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long accountId = (Long) session.getAttribute("loggedInUser");
        if (accountId == null) return "redirect:/login";

        Optional<Account> accountOpt = accountService.findById(accountId);
        if (accountOpt.isEmpty()) return "redirect:/login";

        Account account = accountOpt.get();
        model.addAttribute("phone", account.getPhone());
        return "index";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    /**
     * Hiển thị trang reset password
     */
    @GetMapping("/reset-password")
    public String showResetPasswordForm(Model model) {
        return "reset-password";
    }

    /**
     * Hiển thị trang quên mật khẩu
     */
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        return "forgot-password";
    }
}
