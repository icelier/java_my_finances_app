package lessons.lesson_10_spring_security.controllers.finances.transactions;

import lessons.lesson_10_spring_security.entities.finances.Account;
import lessons.lesson_10_spring_security.entities.finances.Transaction;
import lessons.lesson_10_spring_security.entities.users.UserEntity;
import lessons.lesson_10_spring_security.exceptions.not_found_exception.AccountNotFoundException;
import lessons.lesson_10_spring_security.exceptions.not_found_exception.CategoryNotFoundException;
import lessons.lesson_10_spring_security.exceptions.not_match_exceptions.AccountNotMatchException;
import lessons.lesson_10_spring_security.exceptions.operation_failed.OperationFailedException;
import lessons.lesson_10_spring_security.services.finances.AccountService;
import lessons.lesson_10_spring_security.services.finances.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static lessons.lesson_10_spring_security.MyApplication.BASE_URL;

@RequiredArgsConstructor
@RequestMapping(BASE_URL + "/transactions")
@Controller
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionConverter converter;
    private final AccountService accountService;

    @GetMapping(
            path = "/transfer")
    public String prepareTransfer(Model model,
                                  @RequestParam(name = "accountIdFrom") Long accountIdFrom
    ) {
        Account accountFrom = accountService.findById(accountIdFrom);
        UserEntity user = accountFrom.getUser();

        // remove account from which money transfer will be made from the list of accounts
        // to choose account to transfer money to
        List<Account> userAccounts = user.getAccounts();
        userAccounts.remove(accountFrom);

        model.addAttribute("accounts", (userAccounts));
        model.addAttribute("accountFrom", accountFrom);

        return "transfer";
    }

    @PostMapping(
            path = "/commit")
    public String commitTransaction(RedirectAttributes attrs,
                                    @ModelAttribute(name = "transferRequest") @Valid TransactionRequest request) {
        if (blankInputData(request)) {
            attrs.addAttribute("accountIdFrom", request.getAccountFromId());
            return "redirect:" + BASE_URL + "/transactions/transfer?error";
        }
        try {
            transactionService.commitMoneyTransaction(
                    request.getAccountFromId(),
                    request.getAccountToId(),
                    new BigDecimal(request.getSum())
            );

            return "redirect:" + BASE_URL + "/transactions/" + request.getAccountFromId();
        } catch (OperationFailedException | AccountNotMatchException e) {
            e.printStackTrace();
            attrs.addAttribute("accountIdFrom", request.getAccountFromId());
            return "redirect:" + BASE_URL + "/transactions/transfer?error";
        } catch(CategoryNotFoundException | AccountNotFoundException e) {
            return "redirect:" + BASE_URL + "/?error"; // TODO -check
        }

    }

    @GetMapping(
            path = "/{accountId}")
    public String getAllTransactionsByAccountId(
            Model model,
            @PathVariable(value = "accountId") Long accountId
    ) {
        List<Transaction> transactions = transactionService.findAllByAccountId(accountId);

        List<TransactionResponse> transactionResponses = Collections.emptyList();
        if (transactions != null) {
            transactionResponses = transactions.stream()
                    .map(converter::convertDomainToResponse)
                    .collect(Collectors.toList());
        }

        model.addAttribute("transactions", transactionResponses);

        return "transactions";
    }

    private boolean blankInputData(TransactionRequest request) {
        return request.getSum().isBlank() || request.getAccountFromId() == null ||
                request.getAccountToId() == null;
    }

}
