package lessons.lesson_9_spring_boot.controllers.users.registration;

import lessons.lesson_9_spring_boot.entities.finances.Account;
import lessons.lesson_9_spring_boot.entities.users.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
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
