package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.example.petcareplus.dto.LoginDTO;
import org.example.petcareplus.dto.RegisterDTO;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.service.AccountService;
import org.example.petcareplus.util.PasswordHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AccountController {

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
                           BindingResult result,
                           RedirectAttributes redirectAttributes,
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

        Account account = new Account();
        account.setName(registerDTO.getName());
        account.setPhone(registerDTO.getPhone());
        account.setPassword(PasswordHasher.hash(registerDTO.getPassword()));
        account.setRole("CUSTOMER");
        account.setStatus("ACTIVE");

        accountService.save(account);

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

        if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
            model.addAttribute("error", "Tài khoản bị khóa!");
            return "login";
        }

        Long id = account.getAccountId();

        session.setAttribute("loggedInUser", id);
        return "redirect:/home";
    }


    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) return "redirect:/login";

        model.addAttribute("phone", account.getPhone());
        return "index";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
