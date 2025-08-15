package org.example.petcareplus.controller;

import jakarta.validation.Valid;
import org.example.petcareplus.dto.ResetPasswordDTO;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.repository.AccountRepository;
import org.example.petcareplus.service.ResetPasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controller cho chức năng Reset Password (Quên mật khẩu)
 */
@RestController
@RequestMapping("/api/reset-password")
public class ResetPasswordController {

    private static final Logger log = LoggerFactory.getLogger(ResetPasswordController.class);

    @Autowired
    private ResetPasswordService resetPasswordService;

    @Autowired
    private AccountRepository accountRepository;

    /**
     * Validate thông tin để reset password (không cần OTP)
     */
    @PostMapping("/validate-info")
    public ResponseEntity<?> validateResetPasswordInfo(@RequestParam String username, @RequestParam String phoneNumber) {
        try {
            log.info("🔍 Request to validate reset password info - Username: {}, Phone: {}", username, phoneNumber);

            // Validate input
            if (username == null || username.trim().isEmpty()) {
                return createErrorResponse("Tên đăng nhập không được để trống", 400);
            }
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                return createErrorResponse("Số điện thoại không được để trống", 400);
            }

            // Validate thông tin
            boolean isValid = resetPasswordService.validateUserCredentials(username, phoneNumber);

            if (isValid) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Thông tin hợp lệ, có thể đặt lại mật khẩu");
                response.put("username", username);
                response.put("phoneNumber", phoneNumber);

                log.info("✅ Reset password info validation successful for: {}", username);
                return ResponseEntity.ok(response);
            } else {
                log.warn("❌ Invalid reset password info for: {}", username);
                return createErrorResponse("Họ tên hoặc số điện thoại không khớp với thông tin đăng ký. Vui lòng kiểm tra lại.", 400);
            }

        } catch (Exception e) {
            log.error("❌ Error validating reset password info: {}", e.getMessage());
            return createErrorResponse("Lỗi hệ thống: " + e.getMessage(), 500);
        }
    }

    /**
     * Verify OTP reset password (không cần thiết nữa)
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyResetPasswordOtp(@RequestParam String phoneNumber, @RequestParam String otp) {
        // Luôn return success vì không cần OTP
        log.info("🔍 OTP verification skipped for phone: {} (no OTP required)", phoneNumber);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "OTP verification skipped (no OTP required)");
        response.put("phoneNumber", phoneNumber);

        return ResponseEntity.ok(response);
    }

    /**
     * Reset password
     */
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO, 
                                         BindingResult bindingResult) {
        try {
            log.info("🔄 Request to reset password for user: {}", resetPasswordDTO.getUsername());

            // Validate input
            if (bindingResult.hasErrors()) {
                String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .findFirst()
                    .orElse("Dữ liệu không hợp lệ");
                
                log.warn("❌ Validation errors for reset password: {}", errorMessage);
                return createErrorResponse(errorMessage, 400);
            }

            // Kiểm tra mật khẩu mới và xác nhận có khớp nhau không
            if (!resetPasswordDTO.isPasswordMatch()) {
                log.warn("❌ Password confirmation does not match for user: {}", resetPasswordDTO.getUsername());
                return createErrorResponse("Mật khẩu xác nhận không khớp", 400);
            }

            // Reset password
            boolean success = resetPasswordService.resetPassword(resetPasswordDTO);

            if (success) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Đặt lại mật khẩu thành công");
                response.put("username", resetPasswordDTO.getUsername());

                log.info("✅ Password reset successful for user: {}", resetPasswordDTO.getUsername());
                return ResponseEntity.ok(response);
            } else {
                log.warn("❌ Password reset failed for user: {}", resetPasswordDTO.getUsername());
                return createErrorResponse("Không thể đặt lại mật khẩu. Vui lòng kiểm tra thông tin", 400);
            }

        } catch (Exception e) {
            log.error("❌ Error resetting password for user {}: {}", 
                resetPasswordDTO.getUsername(), e.getMessage());
            return createErrorResponse("Lỗi hệ thống: " + e.getMessage(), 500);
        }
    }

    /**
     * Validate thông tin reset password (không thực hiện reset)
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateResetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO,
                                                 BindingResult bindingResult) {
        try {
            log.info("🔍 Request to validate reset password for user: {}", resetPasswordDTO.getUsername());

            // Validate input
            if (bindingResult.hasErrors()) {
                String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .findFirst()
                    .orElse("Dữ liệu không hợp lệ");
                
                log.warn("❌ Validation errors: {}", errorMessage);
                return createErrorResponse(errorMessage, 400);
            }

            // Validate thông tin
            boolean isValid = resetPasswordService.validateResetPassword(resetPasswordDTO);

            if (isValid) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Thông tin hợp lệ");
                response.put("username", resetPasswordDTO.getUsername());

                log.info("✅ Reset password validation successful for user: {}", resetPasswordDTO.getUsername());
                return ResponseEntity.ok(response);
            } else {
                log.warn("❌ Reset password validation failed for user: {}", resetPasswordDTO.getUsername());
                return createErrorResponse("Thông tin không hợp lệ. Vui lòng kiểm tra lại", 400);
            }

        } catch (Exception e) {
            log.error("❌ Error validating reset password for user {}: {}", 
                resetPasswordDTO.getUsername(), e.getMessage());
            return createErrorResponse("Lỗi hệ thống: " + e.getMessage(), 500);
        }
    }

    /**
     * Kiểm tra account tồn tại theo phone number
     */
    @GetMapping("/check-account")
    public ResponseEntity<?> checkAccountExists(@RequestParam String phoneNumber) {
        try {
            log.info("🔍 Checking if account exists for phone: {}", phoneNumber);

            // Tìm account trong database
            Optional<Account> accountOpt = accountRepository.findByPhone(phoneNumber);
            
            if (accountOpt.isPresent()) {
                Account account = accountOpt.get();
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Account found");
                response.put("accountId", account.getAccountId());
                response.put("name", account.getName());
                response.put("phone", account.getPhone());
                response.put("role", account.getRole());
                response.put("status", account.getStatus());
                response.put("hasProfile", account.getProfile() != null);
                
                log.info("✅ Account found: ID={}, Name={}, Phone={}", 
                    account.getAccountId(), account.getName(), account.getPhone());
                return ResponseEntity.ok(response);
            } else {
                log.warn("❌ No account found for phone: {}", phoneNumber);
                return createErrorResponse("Không tìm thấy tài khoản với số điện thoại này", 404);
            }

        } catch (Exception e) {
            log.error("❌ Error checking account for phone {}: {}", phoneNumber, e.getMessage());
            return createErrorResponse("Lỗi hệ thống: " + e.getMessage(), 500);
        }
    }

    /**
     * Kiểm tra trạng thái service
     */
    @GetMapping("/status")
    public ResponseEntity<?> getServiceStatus() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Reset Password Service is running");
            response.put("timestamp", System.currentTimeMillis());
            response.put("endpoints", new String[]{
                            "POST /api/reset-password/validate-info",
            "POST /api/reset-password/verify-otp", 
            "POST /api/reset-password/reset",
            "POST /api/reset-password/validate",
            "GET /api/reset-password/status",
            "GET /api/reset-password/check-account"
            });

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting service status: {}", e.getMessage());
            return createErrorResponse("Lỗi hệ thống", 500);
        }
    }

    /**
     * Tạo error response
     */
    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, int status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(status).body(errorResponse);
    }
}
