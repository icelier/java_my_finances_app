package lessons.lesson_10_spring_security.controllers.finances.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Accessors(chain = true)
@Data
public class TransactionResponse {
    private String sum;

    private String operation;

    private String timestamp;

    private String accountName;

    private String categoryName;
}
