package lessons.lesson_9_spring_boot.controllers.finances.accounts;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AccountRequest {
    private String name;

    private String total;

    private String accountType;

    private String userName;
}
