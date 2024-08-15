package com.snapfit.main.reservation.application.dto;

import com.snapfit.main.post.application.dto.SnapfitUserSummaryDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Builder
@Getter
public class PostSummaryDto {
    private long id;
    private SnapfitUserSummaryDto maker;

    private String title;
    private String thumbNail;
    private List<String> vibes;
    private List<String> locations;
    private int price;
    private boolean isStudio;
}
