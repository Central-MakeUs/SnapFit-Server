package com.snapfit.main.user.domain;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import reactor.core.publisher.Mono;

@EnableR2dbcRepositories
public interface DeviceRepository extends R2dbcRepository<Device, Long> {
    Mono<Device> findByDeviceId(String deviceId);
}
