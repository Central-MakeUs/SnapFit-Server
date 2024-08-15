package com.snapfit.main.reservation.presentation.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationRequest {

    //TODO valid check
    private String email;
    private String phoneNumber;
    private Long postId;
    private Long makerId;
    private Integer minutes;
    private Integer price;
    private Integer person;
    private Integer personPrice;
    private String reservationLocation;
    private LocalDateTime reservationTime;
}
