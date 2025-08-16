package org.example.petcareplus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO cho chức năng Reset Password (Quên mật khẩu)
 */
public class ResetPasswordDTO {

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3-50 ký tự")
    private String username;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại phải có 10-11 chữ số")
    private String phoneNumber;

    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,50}$",
            message = "Mật khẩu phải có ít nhất 8 và ít hơn 50 ký tự, gồm chữ hoa, chữ thường, số và không chứa khoảng trắng"
    )
    private String newPassword;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword;

    // Constructors
    public ResetPasswordDTO() {}

    public ResetPasswordDTO(String username, String phoneNumber, String newPassword, String confirmPassword) {
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    /**
     * Validate mật khẩu mới và xác nhận mật khẩu có khớp nhau không
     */
    public boolean isPasswordMatch() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }

    /**
     * Format số điện thoại Việt Nam
     */
    public String getFormattedPhoneNumber() {
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

    @Override
    public String toString() {
        return "ResetPasswordDTO{" +
                "username='" + username + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", newPassword='***'" +
                ", confirmPassword='***'" +
                '}';
    }
}
