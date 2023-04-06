package com.github.valhio.storeapi.enumeration;

import lombok.Getter;

import static com.github.valhio.storeapi.constant.Authority.*;

@Getter
public enum Role {
    ROLE_GUEST(GUEST_AUTHORITIES),
    ROLE_USER(USER_AUTHORITIES),
    ROLE_HR(HR_AUTHORITIES),
    ROLE_MANAGER(MANAGER_AUTHORITIES),
    ROLE_ADMIN(ADMIN_AUTHORITIES),
    ROLE_SUPER_ADMIN(SUPER_ADMIN_AUTHORITIES);

    private final String[] authorities;

    Role(String... authorities) {
        this.authorities = authorities;
    }

}
