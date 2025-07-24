package org.example.petcareplus.controller;

import org.example.petcareplus.dto.AccountDTO;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.service.AccountService;
import org.example.petcareplus.util.PasswordHasher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
            model.addAttribute("error", "Số điện thoại đã có tài khoản!");
            model.addAttribute("accounts", accountService.getAllAccount());
            return "account-list";
        }
        account.setPassword(PasswordHasher.hash(account.getPassword()));
        account.setStatus("ACTIVE");
        accountService.save(account);
        return "redirect:/admin/account/list";
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> accountDetail(@PathVariable("id") Long id) {
        Optional<Account> account = accountService.getById(id);

        if (account.isPresent()) {
            AccountDTO dto = new AccountDTO(account.get());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy tài khoản.");
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAccount(@PathVariable("id") Long id, @RequestBody AccountDTO dto) {
        Optional<Account> account = accountService.getById(id);
        if (account.isPresent()) {
            Account acc = account.get();
            acc.setName(dto.getName());
            acc.setPhone(dto.getPhone());
            acc.setRole(dto.getRole());
            acc.setStatus(dto.getStatus());
            accountService.updateAccount(acc);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy tài khoản.");
        }
    }

}
