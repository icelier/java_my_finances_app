package users.entities;

import org.springframework.security.core.userdetails.User;

public class SystemUser extends User {
    private static final long serialVersionUID = 1L;

    public SystemUser(UserEntity user) {
        super(user.getUsername(), user.getPassword(), user.getAuthorities());
    }

}
