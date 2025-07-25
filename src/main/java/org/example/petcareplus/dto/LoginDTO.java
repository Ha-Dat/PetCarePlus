package org.example.petcareplus.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

    public @NotBlank(message = "Mật khẩu không được để trống") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "Mật khẩu không được để trống") String password) {
        this.password = password;
    }

    public @NotBlank(message = "Số điện thoại không được để trống") String getPhone() {
        return phone;
    }

    public void setPhone(@NotBlank(message = "Số điện thoại không được để trống") String phone) {
        this.phone = phone;
    }
}
