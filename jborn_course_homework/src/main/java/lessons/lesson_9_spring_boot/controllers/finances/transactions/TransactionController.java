package lessons.lesson_9_spring_boot.controllers.finances.transactions;

import lessons.lesson_9_spring_boot.controllers.finances.accounts.AccountsController;
import lessons.lesson_9_spring_boot.entities.finances.Operation;
import lessons.lesson_9_spring_boot.entities.finances.Transaction;
import lessons.lesson_9_spring_boot.exceptions.already_exists_exception.TransactionAlreadyExistsException;
import lessons.lesson_9_spring_boot.exceptions.not_found_exception.AccountNotFoundException;
import lessons.lesson_9_spring_boot.exceptions.not_found_exception.CategoryNotFoundException;
import lessons.lesson_9_spring_boot.exceptions.not_match_exceptions.AccountNotMatchException;
import lessons.lesson_9_spring_boot.services.finances.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.internalServerError;
import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RequestMapping("my-finances/transactions")
@RestController
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionConverter converter;

    @PostMapping(
            path = "/commit",
            consumes = { MediaType.APPLICATION_JSON_VALUE},
            produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> commitTransaction(@RequestBody TransactionRequest request) {
        try {
            transactionService.commitTransaction(
                    request.getAccountFromName(),
                    request.getAccountToName(),
                    new BigDecimal(request.getSum())
            );
            String message = request.getSum() + " successfully transferred from " + request.getAccountFromName() +
                    " to " + request.getAccountToName();

            return ok(message);
        } catch(CategoryNotFoundException e) {
            return internalServerError().build();
        } catch (AccountNotFoundException | AccountNotMatchException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping(
            path = "/{userId}",
            produces = { MediaType.APPLICATION_JSON_VALUE })
    public List<TransactionResponse> getAllTransactionsByUserId(
            @PathVariable(value = "userId") Long userId
    ) {
        List<Transaction> transactions = transactionService.findAllByUserId(userId);

        return transactions.stream()
                .map(converter::convertDomainToResponse)
                .collect(Collectors.toList());
    }
}
