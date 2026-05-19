package com.amz.wms.dto.user;

import com.amz.wms.entity.Users;
import com.amz.wms.enumz.UserRoleType;

public record UserResponse(
        String username,
        String email,
        String rolename
) {


    public static UserResponse fromUser(Users users){

        String roleName = UserRoleType.fromRoleId(String.valueOf(users.getRoleid()))
                .map(Enum::name)
                .orElse("UNKNOWN");

        return new UserResponse(
                users.getUsername(),
                users.getEmail(),
                roleName
        );
    }
}
