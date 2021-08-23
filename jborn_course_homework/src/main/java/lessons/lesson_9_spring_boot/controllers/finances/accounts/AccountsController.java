package lessons.lesson_9_spring_boot.controllers.finances.accounts;

import lessons.lesson_9_spring_boot.entities.finances.Account;
import lessons.lesson_9_spring_boot.exceptions.already_exists_exception.AccountAlreadyExistsException;
import lessons.lesson_9_spring_boot.exceptions.not_found_exception.AccountTypeNotFoundException;
import lessons.lesson_9_spring_boot.exceptions.not_found_exception.UserNotFoundException;
import lessons.lesson_9_spring_boot.services.finances.AccountService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.ok;

// for http://localhost:8080/my-finances/accounts POST
@RequiredArgsConstructor
@RequestMapping("/my-finances/accounts")
@RestController
public class AccountsController {

    private final AccountService accountService;
    private final AccountConverter converter;

    @PostMapping(
            path = "/add",
            consumes = { MediaType.APPLICATION_JSON_VALUE},
            produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> addAccount(@RequestBody AccountRequest request) {
        Account account;
        try {
            account = converter.convertDomainFromRequest(request);
        } catch (UserNotFoundException | AccountTypeNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

        try {
            account = accountService.insert(account);
            return ok("Account successfully added!");
        } catch (AccountAlreadyExistsException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(
            path = "/",
            produces = { MediaType.APPLICATION_JSON_VALUE })
    public List<AccountResponse> getAllAccountsByUserId(
            @RequestParam("userId") Long userId
    ) {
        List<Account> userAccounts = accountService.findAllByUserId(userId);

        return userAccounts.stream()
                .map(converter::convertDomainToResponse)
                .collect(Collectors.toList());
    }

}
