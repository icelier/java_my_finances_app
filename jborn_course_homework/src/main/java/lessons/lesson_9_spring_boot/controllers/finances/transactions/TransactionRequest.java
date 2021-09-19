package lessons.lesson_9_spring_boot.controllers.finances.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class TransactionRequest {
    @NotNull @NotBlank private String sum;

    @NotNull private Long accountFromId;

    @NotNull private Long accountToId;
}
