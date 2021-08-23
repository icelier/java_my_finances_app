package lessons.lesson_9_spring_boot.controllers.finances.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionRequest {
    private String sum;

    private String accountFromName;

    private String accountToName;
}
