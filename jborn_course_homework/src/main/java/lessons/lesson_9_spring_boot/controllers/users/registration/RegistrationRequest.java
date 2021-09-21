package lessons.lesson_9_spring_boot.controllers.users.registration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class RegistrationRequest {
    @NotNull @NotBlank private final String userName;
    @NotNull @NotBlank private final String password;
    @NotNull @NotBlank private final String email;
}
