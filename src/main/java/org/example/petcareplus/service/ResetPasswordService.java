package org.example.petcareplus.service;

import org.example.petcareplus.dto.ResetPasswordDTO;

/**
 * Service interface cho chức năng Reset Password
 */
public interface ResetPasswordService {

    /**
     * Validate thông tin reset password
     * @param resetPasswordDTO DTO chứa thông tin reset
     * @return true nếu thông tin hợp lệ
     */
    boolean validateResetPassword(ResetPasswordDTO resetPasswordDTO);

    /**
     * Reset mật khẩu cho user
     * @param resetPasswordDTO DTO chứa thông tin reset
     * @return true nếu reset thành công
     */
    boolean resetPassword(ResetPasswordDTO resetPasswordDTO);

    /**
     * Kiểm tra username và phone number có khớp với database không
     * @param username Tên đăng nhập
     * @param phoneNumber Số điện thoại
     * @return true nếu thông tin khớp
     */
    boolean validateUserCredentials(String username, String phoneNumber);

    /**
     * Gửi OTP xác nhận reset password (nếu cần)
     * @param phoneNumber Số điện thoại
     * @return Message ID của OTP
     */
    String sendResetPasswordOtp(String phoneNumber);

    /**
     * Verify OTP reset password
     * @param phoneNumber Số điện thoại
     * @param otp OTP code
     * @return true nếu OTP đúng
     */
    boolean verifyResetPasswordOtp(String phoneNumber, String otp);
}
