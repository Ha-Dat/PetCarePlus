package org.example.petcareplus.controller;

import jakarta.validation.Valid;
import org.example.petcareplus.dto.AccountDTO;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.enums.AccountRole;
import org.example.petcareplus.enums.AccountStatus;
import org.example.petcareplus.service.AccountService;
import org.example.petcareplus.util.PasswordHasher;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AccountManagementController {

    private final AccountService accountService;

    public AccountManagementController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/account")
    public String getAllAccount(Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size){
        Page<Account> accountPage = accountService.getAccountPage(page, size);
        model.addAttribute("accounts", accountPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", accountPage.getTotalPages());
        model.addAttribute("roles", AccountRole.getRoles());
        model.addAttribute("statuses", AccountStatus.getAllValues());
        return "account-list";
    }

    @PostMapping("/change-status/{id}")
    @ResponseBody
    public ResponseEntity<?> changeAccountStatus(@PathVariable("id") Long id) {
        Optional<Account> account = accountService.getById(id);

        if (!account.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy tài khoản.");
        }

        AccountStatus currentStatus = account.get().getStatus();
        AccountStatus newStatus = (currentStatus == AccountStatus.ACTIVE)
                ? AccountStatus.BANNED
                : AccountStatus.ACTIVE;

        account.get().setStatus(newStatus);
        accountService.updateAccount(account.get());

        return ResponseEntity.ok("Thay đổi trạng thái thành công.");
    }

    @PostMapping("/create")
    public String createAccount(@Valid Account account, Model model, BindingResult result) {

        if (result.hasErrors()) {
            model.addAttribute("error", result.getAllErrors().get(0).getDefaultMessage());
            model.addAttribute("accounts", accountService.getAllAccount());
            model.addAttribute("roles", AccountRole.getRoles());
            model.addAttribute("statuses", AccountStatus.getAllValues());
            return "account-list";
        }

        if (accountService.isPhoneExists(account.getPhone())) {
            model.addAttribute("error", "Số điện thoại đã có tài khoản!");
            model.addAttribute("accounts", accountService.getAllAccount());
            return "account-list";
        }
        account.setPassword(PasswordHasher.hash(account.getPassword()));
        account.setStatus(AccountStatus.ACTIVE);
        accountService.save(account);
        return "redirect:/admin/account";
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> accountDetails(@PathVariable("id") Long id) {
        Optional<Account> account = accountService.getById(id);

        if (account.isPresent()) {
            AccountDTO dto = new AccountDTO(account.get());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy tài khoản.");
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAccount(@PathVariable("id") Long id, @RequestBody AccountDTO dto, BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(result.getAllErrors().get(0).getDefaultMessage());
        }

        Optional<Account> account = accountService.getById(id);
        if (account.isPresent()) {
            Account acc = account.get();
            acc.setName(dto.getName());
            acc.setPhone(dto.getPhone());
            acc.setRole(dto.getRole());
            acc.setStatus(dto.getStatus());

            if (dto.getPassword() != null) {
                acc.setPassword(PasswordHasher.hash(dto.getPassword()));
            }

            accountService.updateAccount(acc);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy tài khoản.");
        }
    }

}
