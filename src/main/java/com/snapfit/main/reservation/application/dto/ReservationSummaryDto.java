package com.snapfit.main.reservation.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReservationSummaryDto {
    private long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Schema(example  = "2024-12-11 12:00:00")
    private LocalDateTime reservationTime;
    private PostSummaryDto post;
    private Integer totalPrice;
    @Schema(description = "null 인 경우 취소되지 않은 데이터")
    private String cancelMessage;
}
