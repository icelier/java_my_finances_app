package lessons.lesson_8_hibernate.entities.users;

import lessons.lesson_8_hibernate.entities.users.UserEntity;

public class UserLoginProjection {
    private Long id;

    private String userName;

    private String password;

    private String email;

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

    public UserLoginProjection() {}

    public UserLoginProjection(Long id, String userName, String password, String email) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.email = email;
    }

    public UserLoginProjection(String userName, String password, String email) {
        this.userName = userName;
        this.password = password;
        this.email = email;
    }

    public UserLoginProjection(UserEntity userEntity) {
        this.id = userEntity.getId();
        this.userName = userEntity.getUserName();
        this.password = userEntity.getPassword();
        this.email = userEntity.getEmail();
    }
}
