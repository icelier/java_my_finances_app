package com.chalova.irina.myfinances.myfinances_client.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import static com.chalova.irina.myfinances.commons_service.PathConstants.*;

@Controller
@RequestMapping()
public class MyFinancesController {

    @Autowired
    private WebClient webClient;

    @Autowired
    DiscoveryClient discoveryClient;

    @Value("${server.port}") private String port;
    @Value("${server.servlet.context-path}") private String contextPath;

    @Value("${realms.auth.addr}") private String realmsUri;
    @Value("${realm.auth.name}") private String realmPath;

    private String logoutRedirectUrl;

    @PostConstruct
    private void initializeLogoutUri() {
        logoutRedirectUrl = "http://" + "localhost:" + port + contextPath + SUCCESS_LOGIN_PATH;
    }

    @GetMapping()
    public String getMyFinancesPage() {

        // for simplicity 'main page' URL just redirects to 'successLoggedIn' page
        return "redirect:" + SUCCESS_LOGIN_PATH;
    }

    @GetMapping(SUCCESS_LOGIN_PATH)
    public String getAfterSuccessLogin() {

        // for simplicity just redirects to user accounts page
        return "redirect:" + ACCOUNTS_BASE_PATH;
    }
//
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();

        return "redirect:" + realmsUri + "/" + realmPath + "/protocol/openid-connect/logout?redirect_uri=" + logoutRedirectUrl;
    }
}
