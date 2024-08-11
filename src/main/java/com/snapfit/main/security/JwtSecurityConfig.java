package com.snapfit.main.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

//jwt 용
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class JwtSecurityConfig {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final ObjectMapper objectMapper;

    private final String[] SWAGGER_PATH= {"/api-docs/**","/swagger/**", "/v2/api-docs", "/v3/api-docs", "/v3/api-docs/**", "/swagger-resources",
            "/swagger-resources/**", "/configuration/ui", "/configuration/security", "/swagger-ui/**",
            "/webjars/**", "/swagger-ui.html"};

    //TODO jwt 필터 적용 필요.
    @Bean
    public SecurityWebFilterChain jwtSecurityFilterChain(ServerHttpSecurity http) throws Exception {
        // CSRF 설정
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityMatcher(new NegatedServerWebExchangeMatcher(ServerWebExchangeMatchers.pathMatchers(SWAGGER_PATH)))
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
//                .sessionManagement()
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        .pathMatchers("/snapfit/login", "/refresh/token", "/snapfit/vibes", "/snapfit/locations")
                        .permitAll()
                        .pathMatchers(HttpMethod.POST, "/snapfit/user")
                        .permitAll()
                        .pathMatchers(SWAGGER_PATH)
                        .permitAll()
                        .anyExchange()
                        .authenticated())
                .exceptionHandling(exceptionHandlingSpec ->
                        exceptionHandlingSpec.authenticationEntryPoint(new CustomAuthenticationEntryPoint(objectMapper)))
                .build();
    }


}

