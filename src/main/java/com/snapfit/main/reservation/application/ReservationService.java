package com.snapfit.main.reservation.application;

import com.snapfit.main.common.dto.PageResult;
import com.snapfit.main.common.exception.ErrorResponse;
import com.snapfit.main.reservation.domain.Reservation;
import com.snapfit.main.reservation.domain.ReservationRepository;
import com.snapfit.main.reservation.domain.exception.ReservationErrorCode;
import com.snapfit.main.reservation.presentation.dto.ReservationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public Mono<Reservation> save(ReservationRequest reservationRequest, long userId) {
        return reservationRepository.save(convertToReservation(reservationRequest, userId));
    }

    public Mono<Reservation> cancel(String message, long reservationId, long userId) {
        return findById(reservationId)
                .filter(reservation -> reservation.getUserId().equals(userId))
                .switchIfEmpty(Mono.error(new ErrorResponse(ReservationErrorCode.FORBIDDEN)))
                .filter(reservation -> reservation.getCancelMessage() == null)
                .switchIfEmpty(Mono.error(new ErrorResponse(ReservationErrorCode.ALREADY_REMOVE)))
                .flatMap(reservation -> reservationRepository.cancel(reservationId, message))
                .then(reservationRepository.findById(reservationId));
    }

    public Mono<Reservation> findById(long id) {
        return reservationRepository.findById(id);
    }

    public Mono<PageResult<Reservation>> findByMakerId(int limit, int offset, long makerId) {
        return reservationRepository.findByMakerId(limit, offset, makerId);
    }

    public Mono<Boolean> findMakerPendingReservation(long makerId) {
        return reservationRepository.findMakerPendingReservation(makerId);
    }
    public Mono<Boolean> findUserPendingReservation(long makerId) {
        return reservationRepository.findUserPendingReservation(makerId);
    }

    public Mono<PageResult<Reservation>> findByUserId(int limit, int offset, long userId) {
        return reservationRepository.findByUserId(limit, offset, userId);
    }

    public Mono<Integer> countByUserId(long userId) {
        return reservationRepository.countByUserId(userId);
    }

    private Reservation convertToReservation(ReservationRequest reservationRequest, long userId) {
        return Reservation.builder()
                .postId(reservationRequest.getPostId())
                .makerId(reservationRequest.getMakerId())
                .userId(userId)
                .phoneNumber(reservationRequest.getPhoneNumber())
                .reservationTime(reservationRequest.getReservationTime())
                .reserveLocation(reservationRequest.getReservationLocation())
                .minutesPrice(reservationRequest.getPrice())
                .minutes(reservationRequest.getMinutes())
                .person(reservationRequest.getPerson())
                .personPrice(reservationRequest.getPersonPrice())
                .email(reservationRequest.getEmail())
                .cancelMessage(null)
                .build();
    }
}
