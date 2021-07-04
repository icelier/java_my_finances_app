package com.chalova.irina.myfinances.user_service.controllers;

import com.chalova.irina.myfinances.user_service.entities.UserEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserController {
    @RequestMapping("/users")
    UserEntity getUserByName(@RequestParam(name = "name") String name);
}
