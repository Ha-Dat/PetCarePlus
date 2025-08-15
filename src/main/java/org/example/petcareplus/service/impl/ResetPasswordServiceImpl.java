package org.example.petcareplus.service.impl;

import org.example.petcareplus.dto.ResetPasswordDTO;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.Profile;
import org.example.petcareplus.repository.AccountRepository;
import org.example.petcareplus.service.ResetPasswordService;
import org.example.petcareplus.util.PasswordHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Implementation của ResetPasswordService
 */
@Service
public class ResetPasswordServiceImpl implements ResetPasswordService {

    private static final Logger log = LoggerFactory.getLogger(ResetPasswordServiceImpl.class);

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public boolean validateResetPassword(ResetPasswordDTO resetPasswordDTO) {
        try {
            log.info("🔍 Validating reset password request for user: {}", resetPasswordDTO.getUsername());

            // Kiểm tra mật khẩu mới và xác nhận có khớp nhau không
            if (!resetPasswordDTO.isPasswordMatch()) {
                log.warn("❌ Password confirmation does not match for user: {}", resetPasswordDTO.getUsername());
                return false;
            }

            // Kiểm tra username và phone number có khớp với database không
            if (!validateUserCredentials(resetPasswordDTO.getUsername(), resetPasswordDTO.getPhoneNumber())) {
                log.warn("❌ Invalid credentials for user: {}", resetPasswordDTO.getUsername());
                return false;
            }

            log.info("✅ Reset password validation successful for user: {}", resetPasswordDTO.getUsername());
            return true;

        } catch (Exception e) {
            log.error("❌ Error validating reset password for user {}: {}", 
                resetPasswordDTO.getUsername(), e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public boolean resetPassword(ResetPasswordDTO resetPasswordDTO) {
        try {
            log.info("🔄 Starting password reset for user: {}", resetPasswordDTO.getUsername());

            // Validate thông tin
            if (!validateResetPassword(resetPasswordDTO)) {
                log.warn("❌ Validation failed for password reset: {}", resetPasswordDTO.getUsername());
                return false;
            }

            // Tìm account trong database
            Optional<Account> accountOpt = accountRepository.findByPhone(resetPasswordDTO.getPhoneNumber());
            if (accountOpt.isEmpty()) {
                log.warn("❌ Account not found for phone: {}", resetPasswordDTO.getPhoneNumber());
                return false;
            }

            Account account = accountOpt.get();

            // Kiểm tra username có khớp không
            if (!account.getName().equals(resetPasswordDTO.getUsername())) {
                log.warn("❌ Username mismatch for account: {}", resetPasswordDTO.getUsername());
                return false;
            }

            // Encode mật khẩu mới bằng PasswordHasher (SHA-256)
            String encodedPassword = PasswordHasher.hash(resetPasswordDTO.getNewPassword());
            
            // Cập nhật mật khẩu
            account.setPassword(encodedPassword);
            accountRepository.save(account);

            log.info("✅ Password reset successful for user: {}", resetPasswordDTO.getUsername());
            return true;

        } catch (Exception e) {
            log.error("❌ Error resetting password for user {}: {}", 
                resetPasswordDTO.getUsername(), e.getMessage());
            return false;
        }
    }

    @Override
    public boolean validateUserCredentials(String username, String phoneNumber) {
        try {
            log.info("🔍 Validating user credentials - Username: {}, Phone: {}", username, phoneNumber);

            // Tìm account trong database theo phone number
            Optional<Account> accountOpt = accountRepository.findByPhone(phoneNumber);
            if (accountOpt.isEmpty()) {
                log.warn("❌ Account not found for phone: {}", phoneNumber);
                return false;
            }

            Account account = accountOpt.get();

            // Kiểm tra phone number có khớp không
            // So sánh trực tiếp với phone trong Account (không format)
            boolean phoneValid = account.getPhone().equals(phoneNumber.trim());
            if (!phoneValid) {
                // Thử format và so sánh lại
                String formattedPhone = formatVietnamPhoneNumber(phoneNumber);
                phoneValid = account.getPhone().equals(formattedPhone);
                
                if (!phoneValid) {
                    log.warn("❌ Phone number mismatch for user: {} (Account phone: {}, Input: {}, Formatted: {})", 
                        username, account.getPhone(), phoneNumber.trim(), formattedPhone);
                    return false;
                }
            }

            // Kiểm tra username có khớp với họ tên trong Account không (case insensitive)
            boolean nameValid = account.getName().equalsIgnoreCase(username.trim());
            if (!nameValid) {
                log.warn("❌ Username '{}' does not match account name '{}'", username, account.getName());
                return false;
            }

            // Kiểm tra xem có Profile không (optional)
            Profile profile = account.getProfile();
            if (profile != null) {
                log.info("✅ Profile found for account: {}", profile.getProfileId());
            }

            log.info("✅ User credentials validation successful for: {} (Name: {}, Phone: {}, Account ID: {})", 
                username, account.getName(), phoneNumber.trim(), account.getAccountId());
            return true;

        } catch (Exception e) {
            log.error("❌ Error validating user credentials for {}: {}", username, e.getMessage());
            return false;
        }
    }

    @Override
    public String sendResetPasswordOtp(String phoneNumber) {
        // Không cần OTP nữa, chỉ cần validate thông tin
        log.info("📱 Reset password request for phone: {} (no OTP required)", phoneNumber);
        return "NO_OTP_" + System.currentTimeMillis();
    }

    @Override
    public boolean verifyResetPasswordOtp(String phoneNumber, String otp) {
        // Luôn return true vì không cần OTP
        log.info("🔍 OTP verification skipped for phone: {} (no OTP required)", phoneNumber);
        return true;
    }

    /**
     * Format số điện thoại Việt Nam
     */
    private String formatVietnamPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return phoneNumber;
        }
        
        String cleanNumber = phoneNumber.trim();
        
        // Nếu đã có +84 rồi thì giữ nguyên
        if (cleanNumber.startsWith("+84")) {
            return cleanNumber;
        }
        
        // Nếu có 84 ở đầu (không có dấu +)
        if (cleanNumber.startsWith("84")) {
            return "+" + cleanNumber;
        }
        
        // Nếu có 0 ở đầu (số Việt Nam thường)
        if (cleanNumber.startsWith("0")) {
            return "+84" + cleanNumber.substring(1);
        }
        
        // Nếu không có gì, thêm +84
        return "+84" + cleanNumber;
    }
}
