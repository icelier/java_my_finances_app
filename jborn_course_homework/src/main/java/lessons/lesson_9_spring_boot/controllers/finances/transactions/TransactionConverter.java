package lessons.lesson_9_spring_boot.controllers.finances.transactions;

import lessons.lesson_9_spring_boot.controllers.DomainConverter;
import lessons.lesson_9_spring_boot.dao.finances.CategoryDao;
import lessons.lesson_9_spring_boot.entities.finances.Transaction;
import lessons.lesson_9_spring_boot.services.finances.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TransactionConverter implements
        DomainConverter<TransactionRequest, Transaction,
        TransactionResponse> {
    private final AccountService accountService;
    private final CategoryDao categoryDao;

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
