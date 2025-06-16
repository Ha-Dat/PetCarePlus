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
                           Model model) {
        if (registeredAccounts.containsKey(phone)) {
            model.addAttribute("error", "Số điện thoại đã tồn tại!");
            return "register";
        }

        registeredAccounts.put(phone, password);
        model.addAttribute("message", "Đăng ký thành công! Hãy đăng nhập.");
        return "login";
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
