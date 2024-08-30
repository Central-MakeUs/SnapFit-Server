package com.snapfit.main.reservation.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationRequest {

    //TODO valid check
    @Email
    @NotBlank
    private String email;

    @Pattern(regexp = "^01\\d{9}$")
    private String phoneNumber;

    @NotNull
    private Long postId;
    @NotNull
    private Long makerId;

    @Min(30)
    @Max(300)
    @NotNull
    private Integer minutes;

    @Min(1000)
    @Max(100_000_000)
    @NotNull
    private Integer price;

    @Min(0)
    @Max(10_000)
    @NotNull
    private Integer person;


    @NotNull
    @Schema(description = "인당 가격")
    private Integer personPrice;

    @Size(min = 1, max = 100)
    @NotEmpty
    private String reservationLocation;

    @Future
    @NotNull
    private LocalDateTime reservationTime;
}
