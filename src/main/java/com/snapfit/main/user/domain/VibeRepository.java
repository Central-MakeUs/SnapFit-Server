package com.snapfit.main.user.domain;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@EnableR2dbcRepositories
public interface VibeRepository extends R2dbcRepository<Vibe, Long> {
}
