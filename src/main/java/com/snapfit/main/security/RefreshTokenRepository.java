package com.snapfit.main.security;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import reactor.core.publisher.Mono;

@EnableR2dbcRepositories
public interface RefreshTokenRepository extends R2dbcRepository<RefreshTokenInfo, Long> {
    Mono<RefreshTokenInfo> findByRefreshToken(String refreshToken);
    Mono<Void> deleteByRefreshToken(String refreshToken);
}
