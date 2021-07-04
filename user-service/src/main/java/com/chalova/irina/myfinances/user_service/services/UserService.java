package com.chalova.irina.myfinances.user_service.services;

import com.chalova.irina.myfinances.user_service.entities.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserEntity findByUserName(String username);
    boolean save(UserEntity user);
}