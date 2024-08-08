package com.snapfit.main.post.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.snapfit.main.post.domain.dto.Price;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class PostDetailDto {

    private Long id;
    private String makerNickName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createAt;
    private String thumbnail;
    private List<String> images;
    private String desc;
    private String title;
    private List<String> vibes;
    private List<String> locations;
    private List<Price> prices;
    private Integer personPrice;
}
