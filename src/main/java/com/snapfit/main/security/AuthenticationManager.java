package com.snapfit.main.security;

import com.snapfit.main.common.exception.ErrorResponse;
import com.snapfit.main.common.exception.enums.CommonErrorCode;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

@Component
@AllArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @SuppressWarnings("unchecked")
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();
        System.out.println(token);
        return Mono.just(token)
                .map(jwtTokenProvider::validateToken)
                .filter(valid -> valid)
                .switchIfEmpty(Mono.empty())
                .map(valid -> {
                    Claims claims = jwtTokenProvider.parseClaims(token);
                    List<String> roles = claims.get("role", List.class);

                    return new UsernamePasswordAuthenticationToken(jwtTokenProvider.getUserId(token), null, roles.stream().map(SimpleGrantedAuthority::new).toList());

                });

    }
}
