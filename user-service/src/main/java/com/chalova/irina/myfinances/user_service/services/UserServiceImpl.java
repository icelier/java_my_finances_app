package com.chalova.irina.myfinances.user_service.services;

import com.chalova.irina.myfinances.user_service.entities.Role;
import com.chalova.irina.myfinances.user_service.entities.SystemUser;
import com.chalova.irina.myfinances.user_service.entities.UserEntity;
import com.chalova.irina.myfinances.user_service.repositories.RoleRepository;
import com.chalova.irina.myfinances.user_service.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    @Autowired
    public void sePasswordEncoder(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserEntity findByUserName(String username) {
        return userRepository.findByUserName(username);
    }

    @Override
    @Transactional
    public boolean save(UserEntity user) {
        UserEntity newUser = new UserEntity();
        if (findByUserName(user.getUserName()) != null) {
            return false;
        }

        newUser.setUserName(user.getUserName());
        newUser.setFullName(user.getFullName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER")));

        userRepository.save(newUser);
        return true;
    }

    @Override
    @Transactional
    public SystemUser loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity fromDatabase = userRepository.findByUserName(username);
        if (fromDatabase == null) {
            throw new UsernameNotFoundException("no such username in the database");
        }

        return buildUserDetails(fromDatabase);
    }

    private SystemUser buildUserDetails(UserEntity user) {
        Collection<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        return new SystemUser(
                user.getUserName(),
                user.getPassword(),
                authorities
        );
    }
}
