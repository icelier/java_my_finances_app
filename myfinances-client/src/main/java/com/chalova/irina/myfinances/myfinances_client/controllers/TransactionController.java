package com.chalova.irina.myfinances.myfinances_client.controllers;

import com.chalova.irina.myfinances.commons_service.dto.AccountDto;
import com.chalova.irina.myfinances.commons_service.dto.TransactionDto;
import com.chalova.irina.myfinances.commons_service.dto.TransferRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import static com.chalova.irina.myfinances.commons_service.PathConstants.*;

@Controller
@RequestMapping(TRANSACTIONS_BASE_PATH)
public class TransactionController {

    @Autowired
    private WebClient webClient;

    @Autowired
    DiscoveryClient discoveryClient;

    @GetMapping(
            path = "/transfer")
    public String prepareTransfer(Model model,
                                  @RequestParam(name = "accountIdFrom") Long accountIdFrom
    ) {
        URI financesUrl = getServiceUri("finances");

        List<AccountDto> accountsList = null;
        if (financesUrl != null) {
            accountsList = webClient.get()
                    .uri(financesUrl + FINANCES_BASE_PATH + ACCOUNTS_BASE_PATH)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<AccountDto>>() {
                    })
                    .block();
        }

        if (accountsList != null && !accountsList.isEmpty()) {
            AccountDto accountFrom = null;
            for (AccountDto account: accountsList) {
                if (account.getId().equals(accountIdFrom)) {
                    accountFrom = account;
                    break;
                }
            }
            if (accountFrom != null) {
                // remove account from the list of accounts
                // to choose toAccount for transfer
                accountsList.remove(accountFrom);

                model.addAttribute("accounts", accountsList);
                model.addAttribute("accountFrom", accountFrom);

                return "transfer";
            }
        }

        return "redirect:" + ACCOUNTS_BASE_PATH + "/?error";
    }

    @PostMapping(
            path = "/commit")
    public String commitTransaction(Model model, RedirectAttributes redirectAttrs,
                                    HttpServletRequest httpRequest,
                                    @ModelAttribute(name = "transferRequest")
                                            TransferRequest transferRequest) {
        if (blankInputData(transferRequest)) {
            redirectAttrs.addAttribute("accountIdFrom", transferRequest.getAccountFromId());
            return "redirect:" + TRANSACTIONS_BASE_PATH +
                    "/transfer" + "?error";
        }

        URI financesUrl = getServiceUri("finances");

        List<TransactionDto> transactionsList = null;
        if (financesUrl != null) {
            transactionsList = webClient.post()
                    .uri(financesUrl + FINANCES_BASE_PATH
                            + TRANSACTIONS_BASE_PATH + "/commit")
                    .body(Mono.just(transferRequest), TransferRequest.class)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<TransactionDto>>() {
                    })
                    .block();
        }

        if (transactionsList != null && !transactionsList.isEmpty()) {
            model.addAttribute("transactions", transactionsList);

            return "transactions";
        } else {
            redirectAttrs.addAttribute("accountIdFrom", transferRequest.getAccountFromId());
            return "redirect:" + TRANSACTIONS_BASE_PATH +
                    "/transfer" + "?error";
        }
    }

    @GetMapping(
            path = "/{accountId}")
    public String getAllTransactionsByAccountId(
            Model model,
            @PathVariable(value = "accountId") Long accountId
    ) {
        URI financesUrl = getServiceUri("finances");

        List<TransactionDto> transactionsList = null;
        if (financesUrl != null) {
            transactionsList = webClient.get()
                    .uri(financesUrl + FINANCES_BASE_PATH + TRANSACTIONS_BASE_PATH,
                    uriBuilder -> uriBuilder
                            .path("/{accountId}")
                            .build(accountId)
                    )
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<TransactionDto>>() {
                    })
                    .block();
        }

        if (transactionsList != null && !transactionsList.isEmpty()) {
            model.addAttribute("transactions", transactionsList);

            return "transactions";
        }
        return "redirect:" + ACCOUNTS_BASE_PATH + "/?error";
    }

    private boolean blankInputData(TransferRequest transferRequest) {
        return transferRequest.getSum() == null || transferRequest.getSum().isBlank()
                || new BigDecimal(transferRequest.getSum()).compareTo(BigDecimal.ZERO) <= 0
                || transferRequest.getAccountFromId() == null
                || transferRequest.getAccountToId() == null || transferRequest.getSum().isBlank();
    }


    private URI getServiceUri(String serviceName) {
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceName);
        URI serviceUri = null;
        if (serviceInstances != null && serviceInstances.size() > 0) {
            serviceUri = serviceInstances.get(0).getUri();
        }

        return serviceUri;
    }

}
