package com.snapfit.main.reservation.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.snapfit.main.post.application.dto.SnapfitUserSummaryDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReservationDetailDto {
    private long id;
    private SnapfitUserSummaryDto user;
    private SnapfitUserSummaryDto maker;
    private PostSummaryDto post;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime reservationTime;
    private String reservationLocation;
    private Integer person;
    private Integer personPrice;
    private Integer basePrice;
    private Integer totalPrice;
    private String cancelMessage;
}
