package com.snapfit.main.common.domain.location;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@EnableR2dbcRepositories
public interface LocationRepository extends R2dbcRepository<Location, Long> {
}
