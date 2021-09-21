package lessons.lesson_10_spring_security.controllers.finances.accounts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Accessors(chain = true)
@Data
public class AccountRequest {
    @NotNull private final String name;

    private String total;

    private final String accountType;

    private String userName;
}
