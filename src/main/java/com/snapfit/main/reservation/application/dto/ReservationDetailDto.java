package com.snapfit.main.reservation.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.snapfit.main.post.application.dto.SnapfitUserSummaryDto;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(example  = "2024-12-11 12:00:00")
    private LocalDateTime reservationTime;

    private String reservationLocation;

    @Schema(description = "추가 인원 수")
    private Integer person;

    @Schema(description = "인당 가격")
    private Integer personPrice;
    @Schema(description = "분당 가격")
    private Integer basePrice;
    @Schema(description = "총 가격")
    private Integer totalPrice;
    @Schema(description = "null 인 경우 취소되지 않은 데이터")
    private String cancelMessage;
}
