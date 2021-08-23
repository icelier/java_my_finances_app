package lessons.lesson_9_spring_boot.controllers.finances.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TransactionResponse {
    private String sum;

    private String operation;

    private String timestamp;

    private String accountName;

    private String categoryName;
}
