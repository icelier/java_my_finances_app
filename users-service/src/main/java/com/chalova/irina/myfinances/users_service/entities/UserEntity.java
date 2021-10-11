package com.chalova.irina.myfinances.users_service.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
public class UserEntity {
    private Long id;

    private String userName;

    private String password;

    private String firstName;

    private String lastName;

    private int age;

    private String email;

    private List<Role> roles;

    public UserEntity(String userName, String password, String email) {
        this.userName = userName;
        this.firstName = userName;
        this.lastName = "";
        this.password = password;
        this.email = email;
    }

    public UserEntity(String userName, String firstName, String password, String email, int age) {
        this(userName, password, email);
        this.firstName = firstName;
        this.age = age;
    }

    public UserEntity(String userName, String firstName, String lastName, String password, String email, int age) {
        this(userName, firstName, password, email, age);
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return getEmail().equals(that.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail());
    }

}