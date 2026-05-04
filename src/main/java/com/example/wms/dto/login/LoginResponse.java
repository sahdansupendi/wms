package com.example.wms.dto.login;

import com.example.wms.entity.Users;
import com.example.wms.enumz.UserRoleType;

public record LoginResponse(
        String username,
        String rolename
) {
    public static LoginResponse fromUser(Users users){
        String roleName = UserRoleType.fromRoleId(String.valueOf(users.getRoleid()))
                .map(Enum::name)
                .orElse("UNKNOWN");

        return new LoginResponse(
                users.getUsername(),
                roleName
        );
    }
}
