package com.snapfit.main.reservation.presentation.dto;

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
    private Integer personPrice;

    @Size(min = 2, max = 100)
    @NotEmpty
    private String reservationLocation;

    @Future
    @NotNull
    private LocalDateTime reservationTime;
}
