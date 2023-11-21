package com.example.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilter(HttpSecurity http) throws Exception {

        http.cors().and().
                csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(authorize ->
                authorize
                        .requestMatchers("/api/v1/users/**").permitAll()
                        .requestMatchers(HttpMethod.PUT,"/api/v1/users/").authenticated()
                        .requestMatchers(HttpMethod.GET,"/api/v1/users/").authenticated()
                        .requestMatchers(HttpMethod.GET,"/api/v1/users/after-login").authenticated()
                        .requestMatchers("/api/v1/file/**").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
        );
        http.oauth2ResourceServer(t -> {
            t.jwt(Customizer.withDefaults());
        });
        http.sessionManagement(t -> t.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();

    }
}

