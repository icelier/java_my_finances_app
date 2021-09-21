package lessons.lesson_10_spring_security.controllers.users.authorization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class RegistrationRequest {
    @NotNull private final String userName;
    @NotNull private final String password;
    @NotNull @ValidEmail private final String email;
}
