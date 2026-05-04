package com.example.wms.enumz;

import com.example.wms.exception.ResourceNotFoundException;

import java.util.Arrays;
import java.util.Optional;

public enum UserRoleType {
    ROLE_SUPERUSER("00"),
    ROLE_ADMIN("01"),
    ROLE_USER("02");

    private final String roleId;

    UserRoleType(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleId() {
        return roleId;
    }

    public static Optional<UserRoleType> fromRoleId(String roleId) {

        if (roleId == null) return Optional.empty();

        return Arrays.stream(values())
                .filter(role -> role.roleId.equals(roleId))
                .findFirst();
    }
}
