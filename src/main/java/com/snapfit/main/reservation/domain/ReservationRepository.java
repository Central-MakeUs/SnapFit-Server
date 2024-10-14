package com.snapfit.main.reservation.domain;

import com.snapfit.main.common.dto.PageResult;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ReservationRepository {
    Mono<Reservation> save(Reservation reservation);
    Mono<PageResult<Reservation>> findByUserId(int limit, int offset, long userId);
    Mono<PageResult<Reservation>> findByMakerId(int limit, int offset, long makerId);
    Mono<Reservation> findById(long id);
    Mono<Integer> countByUserId(long userId);

    Mono<Boolean> cancel(long id, String cancelMessage);

    Mono<Boolean> findMakerPendingReservation(Long makerId);

    Mono<Boolean> findUserPendingReservation(Long makerId);
}
