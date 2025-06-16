package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {

    // Tạm lưu danh sách tài khoản: phone → password
    private Map<String, String> registeredAccounts = new HashMap<>();

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String phone,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           HttpSession session,
                           Model model) {
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$")) {
            model.addAttribute("error", "Mật khẩu phải có ít nhất 8 ký tự, gồm chữ hoa, chữ thường, số và không chứa khoảng trắng.");
            return "register";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp!");
            return "register";
        }

        if (registeredAccounts.containsKey(phone)) {
            model.addAttribute("error", "Số điện thoại đã tồn tại!");
            return "register";
        }

        // Lưu tạm account chờ xác thực OTP
        session.setAttribute("pendingPhone", phone);
        session.setAttribute("pendingPassword", password);

        // Tạo mã OTP ngẫu nhiên
        String otp = String.valueOf((int)((Math.random() * 900000) + 100000));
        session.setAttribute("otp", otp);

        // In OTP ra console (demo) – sau này sẽ tích hợp gửi SMS
        System.out.println("OTP for phone " + phone + ": " + otp);

        return "verify";
    }


    @PostMapping("/verify")
    public String verifyOTP(@RequestParam String otp,
                            HttpSession session,
                            Model model) {
        String sessionOtp = (String) session.getAttribute("otp");
        String phone = (String) session.getAttribute("pendingPhone");
        String password = (String) session.getAttribute("pendingPassword");

        if (sessionOtp == null || phone == null || password == null) {
            return "redirect:/register";
        }

        if (!otp.equals(sessionOtp)) {
            model.addAttribute("error", "Mã OTP không chính xác!");
            return "verify";
        }

        // Lưu vào danh sách chính thức
        registeredAccounts.put(phone, password);
        session.removeAttribute("otp");
        session.removeAttribute("pendingPhone");
        session.removeAttribute("pendingPassword");

        model.addAttribute("message", "Xác minh thành công! Hãy đăng nhập.");
        return "login";
    }

    @GetMapping("/resend-otp")
    public String resendOtp(HttpSession session, Model model) {
        String phone = (String) session.getAttribute("pendingPhone");
        if (phone == null) return "redirect:/register";

        // Tạo mã OTP mới
        String otp = String.valueOf((int)((Math.random() * 900000) + 100000));
        session.setAttribute("otp", otp);
        session.setAttribute("otpTimestamp", System.currentTimeMillis());

        // Hiển thị OTP trong console (tạm thời thay cho SMS)
        System.out.println("Resent OTP for phone " + phone + ": " + otp);

        model.addAttribute("message", "Mã OTP mới đã được gửi lại.");
        return "verify";
    }


    @PostMapping("/login")
    public String login(@RequestParam String phone,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        String savedPassword = registeredAccounts.get(phone);
        if (savedPassword != null && savedPassword.equals(password)) {
            session.setAttribute("phone", phone);
            return "index";
        } else {
            model.addAttribute("error", "Sai số điện thoại hoặc mật khẩu!");
            return "login";
        }
    }


    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String phone = (String) session.getAttribute("phone");
        if (phone == null) {
            return "redirect:/login";
        }
        model.addAttribute("phone", phone);
        return "index";
    }
}
