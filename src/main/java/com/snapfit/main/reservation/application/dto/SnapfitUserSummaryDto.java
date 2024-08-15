package com.snapfit.main.reservation.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SnapfitUserSummaryDto {

    private long id;
    private String nickName;
}
