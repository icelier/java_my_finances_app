package lessons.lesson_5_spring.entities.users;

import java.util.Collection;

public class UserProjection {
    private Long id;

    private String userName;

    private String password;

    private String email;

    private Collection<Role> roles;

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserProjection() {}

    public UserProjection(Long id, String userName,String password, String email) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.email = email;
    }

    public UserProjection(UserEntity userEntity) {
        this.id = userEntity.getId();
        this.userName = userEntity.getUserName();
        this.password = userEntity.getPassword();
        this.email = userEntity.getEmail();
        this.roles = userEntity.getRoles();
    }
}
