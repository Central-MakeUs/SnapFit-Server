package com.snapfit.main.reservation.presentation;

import com.snapfit.main.common.dto.PageResult;
import com.snapfit.main.reservation.adapter.ReservationAdapter;
import com.snapfit.main.reservation.application.dto.ReservationCountDto;
import com.snapfit.main.reservation.application.dto.ReservationDetailDto;
import com.snapfit.main.reservation.application.dto.ReservationSummaryDto;
import com.snapfit.main.reservation.domain.exception.ReservationErrorCode;
import com.snapfit.main.reservation.presentation.dto.ReservationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
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
    @Operation(summary = "예약 생성", description = "예약하는 기능.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "422", description = "입력한 가격이 상품의 가격이랑 다른 경우", content = {@Content(schema = @Schema(implementation = ReservationErrorCode.class))})
    })
    public Mono<ResponseEntity<ReservationDetailDto>> createReservation(Authentication authentication, @Valid @RequestBody ReservationRequest reservationRequest) {
        return reservationAdapter.save(reservationRequest, Long.parseLong(authentication.getName())).map(ResponseEntity::ok);
    }

    @GetMapping("/snapfit/reservation/maker")
    @Operation(summary = "maker의 모든 상품에 대한 예약 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
    })
    public Mono<ResponseEntity<PageResult<ReservationSummaryDto>>> findByMaker(Authentication authentication, @RequestParam("limit") int limit, @RequestParam("offset") int offset, @RequestParam("makerId") long makerId) {
        return reservationAdapter.findByMakerId(limit, offset, makerId, Long.parseLong(authentication.getName())).map(ResponseEntity::ok);
    }

    @GetMapping("/snapfit/reservation/user")
    @Operation(summary = "유저의 예약 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
    })
    public Mono<ResponseEntity<PageResult<ReservationSummaryDto>>> findByUser(Authentication authentication, @RequestParam("limit") int limit, @RequestParam("offset") int offset) {
        return reservationAdapter.findByUserId(limit, offset, Long.parseLong(authentication.getName())).map(ResponseEntity::ok);
    }

    @GetMapping("/snapfit/reservation")
    @Operation(summary = "예약 상세 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "예약이 존재하지 않는 경우", content = {@Content(schema = @Schema(implementation = ReservationErrorCode.class))})
    })
    public Mono<ResponseEntity<ReservationDetailDto>> getReservation(Authentication authentication, @RequestParam("id") long reservationId) {
        return reservationAdapter.findById(reservationId, Long.parseLong(authentication.getName())).map(ResponseEntity::ok);
    }

    @DeleteMapping("/snapfit/reservation")
    @Operation(summary = "예약 취소", description = "예약 취소 메시지는 2글자 이상, 3000자 이하")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "예약이 존재하지 않는 경우", content = {@Content(schema = @Schema(implementation = ReservationErrorCode.class))}),
            @ApiResponse(responseCode = "403", description = "삭제 권한이 없는 경우", content = {@Content(schema = @Schema(implementation = ReservationErrorCode.class))})
    })
    public Mono<ResponseEntity<ReservationDetailDto>> cancelReservation(Authentication authentication, @RequestParam("id") long reservationId, @Valid @Size(min = 2, max = 3000) @RequestParam("message") String message) {
        return reservationAdapter.cancel(message, reservationId, Long.parseLong(authentication.getName())).map(ResponseEntity::ok);
    }

    @GetMapping("/snapfit/reservation/count")
    @Operation(summary = "예약 개수")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
    })
    public Mono<ResponseEntity<ReservationCountDto>> countByUserId(Authentication authentication) {
        return reservationAdapter.countByUserId(Long.parseLong(authentication.getName())).map(ResponseEntity::ok);
    }
}
