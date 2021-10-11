package com.chalova.irina.myfinances.myfinances_client.controllers;

import com.chalova.irina.myfinances.commons_service.dto.AccountDto;
import com.chalova.irina.myfinances.commons_service.dto.AccountTypeDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.View;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import static com.chalova.irina.myfinances.commons_service.PathConstants.ACCOUNTS_BASE_PATH;
import static com.chalova.irina.myfinances.commons_service.PathConstants.FINANCES_BASE_PATH;


@Controller
@RequestMapping(ACCOUNTS_BASE_PATH)
public class AccountsController {

    @Autowired
    private WebClient webClient;

    @Autowired
    DiscoveryClient discoveryClient;

    @GetMapping
    public String getAccountsByUserName(Model model) {
        URI financesUrl = getServiceUrl("finances");

        if (financesUrl != null) {
            List<AccountDto> accounts = this.webClient.get()
                    .uri(financesUrl + FINANCES_BASE_PATH + ACCOUNTS_BASE_PATH)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<AccountDto>>() {
                    })
                    .block();

            model.addAttribute("accounts", accounts);

            return "accounts";
        }

        return "redirect:" + "?error";
    }

    @GetMapping(path = "/new_account")
    public String getAddAccountPage(Model model) {
        URI financesUrl = getServiceUrl("finances");

        if (financesUrl != null) {
            List<AccountTypeDto> accountTypes = this.webClient.get()
                    .uri(financesUrl + FINANCES_BASE_PATH + ACCOUNTS_BASE_PATH + "/new_account")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<AccountTypeDto>>() {
                    })
                    .block();

            model.addAttribute("accountTypes", accountTypes);

            return "add_account";
        }

        return "redirect:" + "?error";
    }

    @PostMapping(
            path = "/add")
    public String addAccount(Model model,
                             @ModelAttribute(name = "newAccount") AccountDto newAccount) throws JsonProcessingException {
        if (!validateNewAccountData(newAccount)) {
            return "redirect:" + ACCOUNTS_BASE_PATH + "/new_account?error";
        }

        URI financesUrl = getServiceUrl("finances");

        List<AccountDto> updatedAccountsList = null;
        if (financesUrl != null) {
            updatedAccountsList = webClient.post()
                    .uri(financesUrl + FINANCES_BASE_PATH + ACCOUNTS_BASE_PATH + "/add")
                    .body(Mono.just(newAccount), AccountDto.class)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<AccountDto>>() {
                    })
                    .block();

            if (updatedAccountsList != null && !updatedAccountsList.isEmpty()) {
                model.addAttribute("accounts", updatedAccountsList);

                return "accounts";
            }
        }

        return "redirect:" + ACCOUNTS_BASE_PATH + "/new_account?error";
    }

    @PostMapping(
            path = "/show/{accountId}"
    )
    public String showAccountDetails(Model model,
                                     @ModelAttribute(value = "account") @Valid AccountDto account,
                                     @PathVariable Long accountId) {

        model.addAttribute("accountToUpdate", account);

        return "update_account";
    }

    @PostMapping(
            path = "/update")
    public String updateAccount(Model model,
            @ModelAttribute(name = "accountToUpdate") AccountDto accountDto) throws JsonProcessingException {

        if (blankInputData(accountDto)) {
            return "redirect:" + ACCOUNTS_BASE_PATH + "/show/" + accountDto.getId() + "?error";
        }

        URI financesUrl = getServiceUrl("finances");

        List<AccountDto> updatedAccountsList = null;
        if (financesUrl != null) {
            updatedAccountsList = webClient.post()
                    .uri( financesUrl + FINANCES_BASE_PATH + ACCOUNTS_BASE_PATH + "/update")
                    .body(Mono.just(accountDto), AccountDto.class)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<AccountDto>>() {
                    })
                    .block();
        }

        if (updatedAccountsList != null && !updatedAccountsList.isEmpty()) {
            model.addAttribute("accounts", updatedAccountsList);

            return "accounts";
        } else {
            return "redirect:" + ACCOUNTS_BASE_PATH + "/show/" + accountDto.getId() + "?error";
        }
    }

    @GetMapping(
            path = "/delete"
    )
    public String deleteAccount(Model model, @RequestParam(name = "accountId") Long accountId) {
        URI financesUrl = getServiceUrl("finances");

        List<AccountDto> updatedAccountsList = null;
        if (financesUrl != null) {
            updatedAccountsList = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(financesUrl + FINANCES_BASE_PATH + ACCOUNTS_BASE_PATH + "/delete")
                            .queryParam("accountId", accountId)
                            .build()
                    )
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<AccountDto>>() {
                    })
                    .block();
        }

        System.out.println("Account list after account update: " + updatedAccountsList);
        if (updatedAccountsList != null && !updatedAccountsList.isEmpty()) {
            model.addAttribute("accounts", updatedAccountsList);

            return "accounts";
        } else {
            return "redirect:" + "?error";
        }
    }

    private boolean blankInputData(AccountDto accountDto) {
        return accountDto.getId() == null || accountDto.getName() == null || accountDto.getTotal() == null ||
                accountDto.getAccountType() == null
                || accountDto.getName().isBlank() || accountDto.getAccountType().isBlank();
    }

    private String getAccountErrorPage(HttpServletRequest httpRequest,
                                       Long id) {
        httpRequest.setAttribute(
                View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        return "redirect:" + ACCOUNTS_BASE_PATH + "/show/" + id + "?error";
    }

    private URI getServiceUrl(String serviceName) {
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceName);

        URI serviceUrl = null;
        if (serviceInstances != null && serviceInstances.size() > 0) {
            serviceUrl = serviceInstances.get(0).getUri();
        }

        return serviceUrl;
    }

    private boolean validateNewAccountData(AccountDto newAccount) {
        if (newAccount.getAccountType() == null || newAccount.getAccountType().isBlank()
                || newAccount.getName() == null || newAccount.getName().isBlank()) {
            return false;
        }

        String total = newAccount.getTotal();
        if (total != null && !total.isEmpty()
                && new BigDecimal(total).compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        if (total == null || total.isEmpty()) {
            newAccount.setTotal("0.00");
        }
        newAccount.setId(0L);

        return true;
    }


}
