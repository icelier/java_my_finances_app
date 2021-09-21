package lessons.lesson_10_spring_security;

import lessons.lesson_10_spring_security.controllers.users.authentication_utils.password_security.CustomAuthenticationFailureHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.context.request.RequestContextListener;

import static lessons.lesson_10_spring_security.MyApplication.BASE_URL;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RequestContextListener requestContextListener(){
        return new RequestContextListener();
    }

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private CustomAuthenticationFailureHandler authenticationFailureHandler;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider =  new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception { // (1)
        auth.authenticationProvider(authProvider());
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.jdbcAuthentication()
//                .dataSource(dataSource)
//                .usersByUsernameQuery(
//                        "SELECT username, password, true as enabled FROM users " +
//                                "WHERE username=?"
//                )
//                .authoritiesByUsernameQuery(
//                        "SELECT u.username, r.name FROM users u " +
//                                "INNER JOIN users_roles ur ON u.id=ur.user_id " +
//                                "INNER JOIN roles r ON ur.role_id=r.id " +
//                                "WHERE username=?"
//                ).passwordEncoder(passwordEncoder());
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(BASE_URL + "/users/authorize").permitAll()
                .antMatchers(BASE_URL + "/users/register").permitAll()
                .antMatchers(
                        BASE_URL + "/users/*",
                        BASE_URL + "/accounts/*",
                        BASE_URL + "/transactions/*"
                ).authenticated()
                .and().formLogin().loginPage(BASE_URL + "/users/login")
                // does not work
//                .failureHandler(authenticationFailureHandler)
//                .failureUrl(BASE_URL + "/users/login?error")
                .permitAll()
                .loginProcessingUrl("/authenticateTheUser")
                .successForwardUrl(BASE_URL + "/accounts")
                .and()
                .logout().logoutUrl("/logout").invalidateHttpSession(true).permitAll();

    }
}
