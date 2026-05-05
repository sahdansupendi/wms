package com.example.wms.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserGetByidRequest(
        @NotBlank
        String userid
) {
}
