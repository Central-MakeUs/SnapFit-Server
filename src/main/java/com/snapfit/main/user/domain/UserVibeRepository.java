package com.snapfit.main.user.domain;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import reactor.core.publisher.Mono;

@EnableR2dbcRepositories
public interface UserVibeRepository extends R2dbcRepository<UserVibe, Long> {
    Mono<Void> deleteByUserId(long userId);
}
