package org.example.petcareplus.controller;

import org.example.petcareplus.entity.Account;
import org.example.petcareplus.service.AccountService;
import org.example.petcareplus.util.PasswordHasher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/account")
public class AccountManagementController {

    private final AccountService accountService;

    public AccountManagementController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/list")
    public String getAllAccount(Model model){
        List<Account> accounts = accountService.getAllAccount();
        model.addAttribute("accounts", accounts);
        return "account-list";
    }

    @PostMapping("/ban/{id}")
    public String banOrUnbanAccount(@PathVariable("id") Long id) {
        Optional<Account> account = accountService.getById(id);
        if (account.isPresent()) {
            Account acc = account.get();
            if (acc.getStatus().equals("ACTIVE")) {
                acc.setStatus("BANNED");
            } else if (acc.getStatus().equals("BANNED")) {
                acc.setStatus("ACTIVE");
            }
            accountService.updateAccount(acc);
        }
        return "redirect:/admin/account/list";
    }

    @PostMapping("/create")
    public String createAccount(Account account, Model model) {
        if (accountService.isPhoneExists(account.getPhone())) {
            model.addAttribute("error", "Số điện thoại có tài khoản!");
            model.addAttribute("accounts", accountService.getAllAccount());
            return "account-list";
        }
        account.setPassword(PasswordHasher.hash(account.getPassword()));
        account.setStatus("ACTIVE");
        accountService.save(account);
        return "redirect:/admin/account/list";

    }

}
