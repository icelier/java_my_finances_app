package lessons.lesson_10_spring_security.controllers.finances.accounts;

import lessons.lesson_10_spring_security.controllers.users.authentication_utils.IAuthenticationFacade;
import lessons.lesson_10_spring_security.dao.finances.AccountTypeDao;
import lessons.lesson_10_spring_security.entities.finances.Account;
import lessons.lesson_10_spring_security.entities.finances.AccountType;
import lessons.lesson_10_spring_security.entities.users.UserEntity;
import lessons.lesson_10_spring_security.exceptions.already_exists_exception.AccountAlreadyExistsException;
import lessons.lesson_10_spring_security.exceptions.not_found_exception.AccountNotFoundException;
import lessons.lesson_10_spring_security.exceptions.not_found_exception.AccountTypeNotFoundException;
import lessons.lesson_10_spring_security.exceptions.not_found_exception.UserNotFoundException;
import lessons.lesson_10_spring_security.exceptions.operation_failed.OperationFailedException;
import lessons.lesson_10_spring_security.services.finances.AccountService;
import lessons.lesson_10_spring_security.services.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static lessons.lesson_10_spring_security.MyApplication.BASE_URL;
import static org.springframework.http.ResponseEntity.internalServerError;

@RequiredArgsConstructor
@RequestMapping(BASE_URL + "/accounts")
@Controller
public class AccountsController {
    @Autowired
    private IAuthenticationFacade authenticationFacade;

    private final AccountService accountService;
    private final AccountConverter converter;
    private final AccountTypeDao accountTypeDao;
    private final UserService userService;

    @PostMapping()
    public String getAllAccountsByUserId(Model model) {
        String userName = ((User)authenticationFacade.getAuthentication().getPrincipal()).getUsername();
        UserEntity user = userService.findByUserName(userName);

        List<Account>  userAccounts = user.getAccounts();
        List<AccountResponse> accountResponses = userAccounts.stream()
                    .map(converter::convertDomainToResponse)
                    .collect(Collectors.toList());

        model.addAttribute("accounts", accountResponses);

        return "accounts";
    }

    // TODO - add test
    @GetMapping(path = "/new_account")
    public String getAddAccountPage(Model model) {
        List<AccountType> types = accountTypeDao.findAll();
        model.addAttribute("accountTypes", types);

        return "add_account";
    }

    @PostMapping(
            path = "/add")
    public String addAccount(HttpServletRequest httpRequest,
                             @ModelAttribute(name = "newAccount") @Valid AccountRequest request) {
        if (blankInputData(request)) {
            return "redirect:" + BASE_URL + "/accounts/new_account?error";
        }
        if (request.getTotal() == null || request.getTotal().isEmpty()) {
            request.setTotal("0.00");
        }

        String userName = ((User) authenticationFacade.getAuthentication().getPrincipal()).getUsername();
        UserEntity user = userService.findByUserName(userName);
        request.setUserName(user.getUserName());

        Account account;
        try {
            account = converter.convertDomainFromRequest(request);
        } catch (UserNotFoundException | AccountTypeNotFoundException e) {
            return "redirect:" + BASE_URL + "/accounts/new_account?error";
        }

        try {
            accountService.insert(account);
            httpRequest.setAttribute(
                    View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
            return "redirect:" + BASE_URL + "/accounts";
        } catch (AccountAlreadyExistsException e) {
            e.printStackTrace();
            return "redirect:" + BASE_URL + "/accounts/new_account?error";
        }
    }

    @GetMapping(
            path = "/show/{accountId}"
    )
    public String showAccountDetails(Model model,
                                     @PathVariable Long accountId) {
        Account account = accountService.findById(accountId);
        AccountRequest accountRequest = new AccountRequest(
                account.getName(),
                account.getTotal().toString(),
                account.getType().getTitle(),
                ""
        );

        model.addAttribute("accountToUpdate", accountRequest);
        model.addAttribute("accountId", accountId);

        return "update_account";
    }

    @PostMapping(
            path = "/update")
    public String updateAccount(
            HttpServletRequest httpRequest,
            @ModelAttribute(name = "accountToUpdate") @Valid AccountRequest request,
                                                         @RequestParam(value = "accountId") Long accountId) {
        if (blankInputData(request)) {
            return "redirect:" + BASE_URL + "/accounts/show/" + accountId + "?error";
        }

        String userName = ((User)authenticationFacade.getAuthentication().getPrincipal()).getUsername();
        request.setUserName(userName);

        Account updateData = null;
        try {
            updateData = converter.convertDomainFromRequest(request);
        } catch (UserNotFoundException | AccountTypeNotFoundException e) {
            return "redirect:" + BASE_URL + "/accounts/show/" + accountId + "?error";
        }

        try {
            accountService.update(accountId, updateData);

            httpRequest.setAttribute(
                    View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
            return "redirect:" + BASE_URL + "/accounts";
        } catch (AccountNotFoundException e) {
            e.printStackTrace();
            return "redirect:" + BASE_URL + "/accounts/show/" + accountId + "?error";
        }
    }

    @PostMapping(
            path = "/delete"
    )
    public String deleteAccount(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
                                @RequestParam(name = "accountId") Long id) {
        try {
            accountService.deleteById(id);
        } catch (AccountNotFoundException | OperationFailedException e) {
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return e.getMessage();
        }

        httpRequest.setAttribute(
                View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        return "redirect:" + BASE_URL + "/accounts";
    }

    private boolean blankInputData(AccountRequest request) {
        return request.getName().isBlank() || request.getAccountType().isBlank();
    }

    private String getAccountErrorPage(HttpServletRequest httpRequest,
                                       Long id) {
        httpRequest.setAttribute(
                View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        return "redirect:" + BASE_URL + "/accounts/show/" + id + "?error";
    }
}
