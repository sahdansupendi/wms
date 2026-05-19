package com.amz.wms.dto.auth;

import com.amz.wms.entity.Users;
import com.amz.wms.enumz.UserRoleType;

import java.util.Date;

public record AuthResponse(
        String username,
        String rolename,
        String token,
        Date expiredAt,
        String refreshToken
) {
    public static AuthResponse fromUser(Users users, String token, Date expiredAt,String refreshToken){
        String roleName = UserRoleType.fromRoleId(String.valueOf(users.getRoleid()))
                .map(Enum::name)
                .orElse("UNKNOWN");

        return new AuthResponse(
                users.getUsername(),
                roleName,
                token,
                expiredAt,
                refreshToken
        );
    }
}
