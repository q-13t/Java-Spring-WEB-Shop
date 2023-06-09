package com.test.learn.godbless.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

    @Autowired
    private CustomEntryPoint customEntryPoint;

    @Bean
    SecurityFilterChain httpSecurity(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/purchase", "/confirmPurchase", "/user/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/", "/**", "/error/**", "/logout", "/register", "/css/**", "/js/**",
                        "/imgs/**",
                        "/imgs/favicon/**", "/validateUser")
                .permitAll()
                .and()
                .formLogin(login -> login.loginPage("/hello").permitAll())
                .logout(logout -> logout.permitAll().logoutSuccessUrl("/"))
                .exceptionHandling(handling -> handling.accessDeniedPage("/error/forbidden").authenticationEntryPoint(
                        customEntryPoint))
                .build();
    }

    @Bean
    @Autowired
    JdbcUserDetailsManager userDetailsManager(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public String getAttribute() {
        return null;
    }
}
