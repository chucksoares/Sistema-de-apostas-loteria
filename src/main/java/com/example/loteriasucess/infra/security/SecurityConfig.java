package com.example.loteriasucess.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/loterias/jogos").permitAll()       // Permite acesso público
                        .requestMatchers("/loterias/lotofacil").permitAll()
                        .requestMatchers("/loterias/lotofacil/predictions").permitAll()
                        .anyRequest().authenticated()                        // Requer autenticação para outros endpoints
                )
                .csrf(csrf -> csrf.disable())                            // Desativa CSRF
                .httpBasic(withDefaults());                               // Configura HTTP Basic

        return http.build();
    }
}
