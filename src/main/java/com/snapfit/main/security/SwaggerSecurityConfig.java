package com.snapfit.main.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SwaggerSecurityConfig {

    @Value("${security.swagger.user}")
    private String swaggerUser;

    @Value("${security.swagger.password}")
    private String swaggerPassword;

    //TODO 로그인 기능 구현 후, 수정 필요. 또한 분산 서비스가 되면, 잘 작동안 할 것 같음.
    @Bean
    @Order(1)
    public SecurityWebFilterChain swaggerSecurityFilterChain(ServerHttpSecurity http) throws Exception {
        // CSRF 설정
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        .pathMatchers("/swagger/**", "/v2/api-docs", "/v3/api-docs", "/v3/api-docs/**", "/swagger-resources",
                                "/swagger-resources/**", "/configuration/ui", "/configuration/security", "/swagger-ui/**",
                                "/webjars/**", "/swagger-ui.html")
                        .hasRole("SWAGGER_USER")
                        .anyExchange().permitAll())
                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        System.out.println(swaggerPassword);
        UserDetails user = User.builder()
                .username(swaggerUser)
                .password(passwordEncoder().encode(swaggerPassword))
                .roles("SWAGGER_USER")
                .build();
        return new MapReactiveUserDetailsService(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



}
