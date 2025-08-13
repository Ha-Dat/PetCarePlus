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

    @GetMapping("/verify")
    public String showVerifyPage() {
        return "verify";
    }

    @PostMapping("/verify")
    public String verifyOtp(@RequestParam("otp") String otpInput,
                            RedirectAttributes redirectAttributes,
                            HttpSession session,
                            Model model) {
        Long accountId = (Long) session.getAttribute("loggedInUser");
        if (accountId == null) return "redirect:/login";

        Optional<Account> accountOpt = accountService.findById(accountId);
        if (accountOpt.isEmpty()) return "redirect:/login";

        Account account = accountOpt.get();

        if (account.getOtp() == null || account.getOtpExpiry() == null ||
                account.getOtpExpiry().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Mã OTP đã hết hạn. Vui lòng gửi lại.");
            return "verify";
        }

        if (!account.getOtp().equals(otpInput)) {
            model.addAttribute("error", "Mã OTP không chính xác.");
            return "verify";
        }

        // Nếu đúng thì ACTIVE và xóa OTP
        account.setStatus(AccountStatus.ACTIVE);
        account.setOtp(null);
        account.setOtpExpiry(null);
        accountService.save(account);

        session.removeAttribute("loggedInUser");
        redirectAttributes.addFlashAttribute("message", "Đăng ký thành công! Hãy đăng nhập.");

        return "redirect:/login";
    }

    @GetMapping("/resend-otp")
    public String resendOtp(HttpSession session, RedirectAttributes redirectAttributes) {
        Long accountId = (Long) session.getAttribute("loggedInUser");
        if (accountId == null) return "redirect:/login";

        Optional<Account> accountOpt = accountService.findById(accountId);
        if (accountOpt.isEmpty()) return "redirect:/login";

        Account account = accountOpt.get();

        // Tạo OTP mới
        String newOtp = String.format("%06d", (int)(Math.random() * 1_000_000));
        account.setOtp(newOtp);
        account.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

        accountService.save(account);

        String otp = String.format("%06d", (int)(Math.random() * 1_000_000));
        account.setOtp(otp);
        account.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        account.setStatus(AccountStatus.ACTIVE);
        accountService.save(account);

        // Gửi OTP qua SMS hoặc log ra console (tạm thời)
        System.out.println("Mã OTP mới của bạn: " + newOtp);

        redirectAttributes.addFlashAttribute("message", "Mã OTP đã được gửi lại.");
        return "redirect:/verify";
    }


    @PostMapping("/register")
    public String register(@ModelAttribute("registerDTO") @Valid RegisterDTO registerDTO,
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

        // Tạo tài khoản với trạng thái PENDING
        Account account = new Account();
        account.setName(registerDTO.getName());
        account.setPhone(registerDTO.getPhone());
        account.setPassword(PasswordHasher.hash(registerDTO.getPassword()));
        account.setRole(AccountRole.CUSTOMER);
        account.setStatus(AccountStatus.ACTIVE);

        // Sinh OTP
        String otp = String.format("%06d", (int)(Math.random() * 1_000_000));
        account.setOtp(otp);
        account.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        account.setStatus(AccountStatus.ACTIVE); // chưa kích hoạt

        accountService.save(account);

        // Log OTP ra console (tạm thời)
        System.out.println("OTP của bạn là: " + otp);

        // Lưu vào session để verify
        session.setAttribute("loggedInUser", account.getAccountId());

        // Tạo hồ sơ người dùng nếu chưa có
        Profile existingProfile = accountService.getProfileByAccountAccountId(account.getAccountId());
        if (existingProfile == null) {
            existingProfile = new Profile();
            existingProfile.setAccount(account);
        }
        accountService.profileSave(existingProfile);
        return "redirect:/verify";
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
                return "redirect:/vet/dashboard";
            }
            case PET_GROOMER -> {
                return "redirect:/pet-groomer/dashboard";
            }
            case MANAGER -> {
                return "redirect:/manager/dashboard";
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
}
