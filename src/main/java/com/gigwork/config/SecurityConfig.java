
package com.gigwork.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationManager authenticationManager)
            throws Exception {

        http.authenticationManager(authenticationManager);

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                        "/", "/register", "/login",
                        "/css/**", "/js/**", "/images/**",
                        "/favicon.ico", "/error"
                ).permitAll()

                .requestMatchers("/admin/**").hasRole("ADMIN")

                .requestMatchers("/applications/**").authenticated()

                .requestMatchers(
                        "/dashboard", "/dashboard/**",
                        "/profile/**", "/my-applications/**",
                        "/gigs", "/gigs/**"
                ).authenticated()

                .anyRequest().authenticated()
        );

        http.formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error")
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
        );

        return http.build();
    }
}
