package com.chalova.irina.myfinances.user_service.entities;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class SystemUser extends User {
    private static final long serialVersionUID = 1L;

    public SystemUser(String userName, String password, Collection<? extends GrantedAuthority> authorities) {
        super(userName, password, authorities);
    }

}
