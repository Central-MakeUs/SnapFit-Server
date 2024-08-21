package com.snapfit.main.post.presentation.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.snapfit.main.post.domain.dto.Price;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreatePostRequest {

    @Size(min = 1, max = 2)
    @NotNull
    @Schema(description = "상품 분위기. 최소 1개, 최대 2개.")
    private List<String> vibes;

    @Size(min = 1, max = 2)
    @NotNull
    @Schema(description = "상품 촬영 가능 장소. 최소 1개, 최대 2개.")
    private List<String> locations;

    @Size(min = 1, max = 8)
    @NotEmpty
    @Schema(description = "사진 이름. signed url 을 생성했을 때 준 파일 이름을 줘야 한다.")
    private List<String> imageNames;

    @NotEmpty
    @Schema(description = "썸네일 사진 이름. signed url 을 생성했을 때 준 파일 이름을 줘야 한다.")
    private String thumbnail;

    @NotNull
    @Size(min = 1, max = 128)
    private String title;

    @NotNull
    @Size(min = 1, max = 4000)
    private String desc;

    @NotNull
    private boolean isStudio;

    @Size(min = 1)
    @NotNull
    @Schema(description = "분에 해당하는 가격")
    private List<Price> prices;

    @Min(0)
    @Max(100_000_000)
    @Schema(description = "사람 한 명 당 가격")
    private Integer personPrice;

}
