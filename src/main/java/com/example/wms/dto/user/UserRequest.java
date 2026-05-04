package com.example.wms.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @NotBlank(message = "Username cannot be blank") String username,
        @Email(message = "Email must be valid") String email,
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
