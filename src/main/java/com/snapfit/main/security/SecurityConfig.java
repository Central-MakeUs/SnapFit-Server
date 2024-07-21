package com.snapfit.main.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;

//jwt 용
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    //TODO jwt 필터 적용 필요.
    @Bean
    @Order(2)
    public SecurityWebFilterChain jwtSecurityFilterChain(ServerHttpSecurity http) throws Exception {
        // CSRF 설정
        http.csrf(csrfSpec -> csrfSpec.disable())
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec.anyExchange().permitAll());

        return http.build();
    }

}

