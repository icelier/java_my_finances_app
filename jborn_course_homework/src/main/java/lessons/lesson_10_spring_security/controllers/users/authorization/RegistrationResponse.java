package lessons.lesson_10_spring_security.controllers.users.authorization;

import lessons.lesson_10_spring_security.entities.finances.Account;
import lessons.lesson_10_spring_security.entities.users.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class RegistrationResponse {
    private Long id;

    private String userName;

    private String fullName;

    private int age;

    private String email;

    private List<Account> accounts;

    private List<Role> roles;
}
