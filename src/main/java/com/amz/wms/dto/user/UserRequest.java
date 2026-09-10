package com.amz.wms.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserRequest(
        @NotBlank(message = "Username cannot be blank") String username,
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email must be valid")
        @Pattern(regexp = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$", message = "Format email tidak valid, contoh: user@example.com")
        String email,
        @NotBlank(message = "Password cannot be blank") String password,
        @NotBlank(message = "Roleid cannot be blank") String roleid
) {
    public UserRequest {
        if (email != null){
            email = email.trim().toLowerCase();
        }

        if (username != null){
            username = username.trim().toLowerCase();
        }
    }
}
