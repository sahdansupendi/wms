package com.amz.wms.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserUpdateRequest(
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email Must be valid")
        @Pattern(regexp = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$", message = "Format email tidak valid, contoh: user@example.com")
        String email,
        @NotBlank(message = "Username cannot be blank") String username,
        String roleid
) {
    public UserUpdateRequest{
        if (email != null) {
            email = email.trim().toLowerCase();
        }

        if (username != null){
            username = username.trim().toLowerCase();
        }
    }
}
