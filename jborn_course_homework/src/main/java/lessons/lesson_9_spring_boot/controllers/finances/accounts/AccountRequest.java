package lessons.lesson_9_spring_boot.controllers.finances.accounts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Accessors(chain = true)
@Data
public class AccountRequest {
    @NotNull @NotBlank private String name;

    @NotNull @NotBlank private String total;

    @NotNull @NotBlank private String accountType;

    @NotNull @NotBlank private String userName;
}
