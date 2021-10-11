package com.chalova.irina.myfinances.finance_service;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;

import static com.chalova.irina.myfinances.commons_service.PathConstants.ACCOUNTS_BASE_PATH;
import static com.chalova.irina.myfinances.commons_service.PathConstants.TRANSACTIONS_BASE_PATH;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class HttpSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .authorizeRequests()
                .antMatchers(
                        HttpMethod.GET,
                        ACCOUNTS_BASE_PATH + "/**",
                        TRANSACTIONS_BASE_PATH + "/**")
                .hasAuthority("SCOPE_read")
                .antMatchers(
                        HttpMethod.POST,
                        ACCOUNTS_BASE_PATH + "/**",
                        TRANSACTIONS_BASE_PATH + "/**")
                .hasAuthority("SCOPE_write")
                .anyRequest()
                .authenticated()
                .and()
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);


    }
}

