package com.amz.wms.enumz;

import java.util.Arrays;
import java.util.Optional;

public enum UserRoleType {
    SUPERUSER("00"),
    ADMIN("01"),
    USER("02");

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
