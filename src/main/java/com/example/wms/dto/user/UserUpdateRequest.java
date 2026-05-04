package com.example.wms.dto.user;

import jakarta.validation.constraints.Email;

public record UserUpdateRequest(
        @Email(message = "Email Must be valid")
        String email,
        String username,
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
