package lessons.lesson_10_spring_security.controllers.finances.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class TransactionRequest {
    @NotNull private final String sum;

    private final Long accountFromId;

    private final Long accountToId;
}
