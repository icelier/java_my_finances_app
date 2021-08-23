package lessons.lesson_9_spring_boot.controllers.finances.transactions;

import lessons.lesson_9_spring_boot.controllers.DomainConverter;
import lessons.lesson_9_spring_boot.entities.finances.Transaction;
import lessons.lesson_9_spring_boot.services.finances.AccountService;
import lessons.lesson_9_spring_boot.services.finances.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class TransactionConverter implements
        DomainConverter<TransactionRequest, Transaction,
        TransactionResponse> {
    private final AccountService accountService;
    private final CategoryService categoryService;

    @Override
    public Transaction convertDomainFromRequest(TransactionRequest request){

        return null;
    }

    @Override
    public TransactionResponse convertDomainToResponse(Transaction transaction) {
        return new TransactionResponse(transaction.getSum().toString(),
                transaction.getOperation().name(),
                transaction.getTimestamp().toString(),
                transaction.getAccount().getName(),
                transaction.getCategory().getTitle());
    }
}
