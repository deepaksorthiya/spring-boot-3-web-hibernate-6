package com.example.mapper;

import com.example.dto.AppUserDto;
import com.example.dto.RoleDto;
import com.example.entity.AppUser;
import com.example.entity.Role;

public final class DtoMapper {

    private DtoMapper() {
    }

    public static AppUserDto toUserResponse(AppUser u) {
        return new AppUserDto(u.getUserId(), u.getEmail(), u.getPassword(), u.getFirstName(), u.getLastName());
    }

    public static RoleDto toRoleResponse(Role role) {
        return new RoleDto(role.getRoleId(), role.getRoleName(), role.getRoleDesc());
    }
}
