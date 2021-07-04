package com.chalova.irina.myfinances.user_service.controllers;

import com.chalova.irina.myfinances.user_service.services.UserService;
import com.chalova.irina.myfinances.user_service.entities.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserControllerImpl implements UserController {
//    @Autowired
//    @Lazy
//    private EurekaClient eurekaClient;

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserEntity getUserByName(@RequestParam(name = "name") String name) {
        return userService.findByUserName(name);
    }
}
