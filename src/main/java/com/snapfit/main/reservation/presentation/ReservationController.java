package com.snapfit.main.reservation.presentation;

import com.snapfit.main.common.dto.PageResult;
import com.snapfit.main.reservation.adapter.ReservationAdapter;
import com.snapfit.main.reservation.application.dto.ReservationDetailDto;
import com.snapfit.main.reservation.application.dto.ReservationSummaryDto;
import com.snapfit.main.reservation.presentation.dto.ReservationRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@SecurityRequirement(name = "Bearer Authentication")
public class ReservationController {

    private final ReservationAdapter reservationAdapter;

    @PostMapping("/snapfit/reservation")
    public Mono<ResponseEntity<ReservationDetailDto>> createReservation(Authentication authentication, @RequestBody ReservationRequest reservationRequest) {
        return reservationAdapter.save(reservationRequest, Long.parseLong(authentication.getName())).map(ResponseEntity::ok);
    }

    @GetMapping("/snapfit/reservation/maker")
    public Mono<ResponseEntity<PageResult<ReservationSummaryDto>>> findByMaker(Authentication authentication, @RequestParam("limit") int limit, @RequestParam("offset") int offset, @RequestParam("makerId") long makerId) {
        return reservationAdapter.findByMakerId(limit, offset, makerId).map(ResponseEntity::ok);
    }

    @GetMapping("/snapfit/reservation/user")
    public Mono<ResponseEntity<PageResult<ReservationSummaryDto>>> findByUser(Authentication authentication, @RequestParam("limit") int limit, @RequestParam("offset") int offset) {
        return reservationAdapter.findByUserId(limit, offset, Long.parseLong(authentication.getName())).map(ResponseEntity::ok);
    }

    @GetMapping("/snapfit/reservation")
    public Mono<ResponseEntity<ReservationDetailDto>> getReservation(Authentication authentication, @RequestParam("id") long reservationId) {
        return reservationAdapter.findById(reservationId).map(ResponseEntity::ok);
    }

    @DeleteMapping("/snapfit/reservation")
    public Mono<ResponseEntity<ReservationDetailDto>> cancelReservation(Authentication authentication, @RequestParam("id") long reservationId, @RequestParam("message") String message) {
        return reservationAdapter.cancel(message, reservationId, Long.parseLong(authentication.getName())).map(ResponseEntity::ok);
    }
}
