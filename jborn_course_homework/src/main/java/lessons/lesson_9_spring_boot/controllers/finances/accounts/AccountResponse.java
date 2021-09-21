package lessons.lesson_9_spring_boot.controllers.finances.accounts;

import lessons.lesson_9_spring_boot.controllers.finances.transactions.TransactionResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@AllArgsConstructor
@Accessors(chain = true)
@Data
public class AccountResponse {
    private String name;

    private String total;

    private String accountType;

    private String userName;

    private List<TransactionResponse> transactions;
}
