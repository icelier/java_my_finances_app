package lessons.lesson_10_spring_security.controllers.finances.accounts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Data
public class AccountResponse {
    private Long id;

    private String name;

    private String total;

    private String accountType;
}
