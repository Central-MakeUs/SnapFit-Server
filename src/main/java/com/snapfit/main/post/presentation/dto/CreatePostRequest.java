package com.snapfit.main.post.presentation.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.snapfit.main.post.domain.dto.Price;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreatePostRequest {

//    @Size(min = 1, max = 2)
    @NotNull
    private List<String> vibes;

//    @Size(min = 1, max = 2)
    @NotNull
    private List<String> locations;

    @Size(min = 1, max = 8)
    @NotNull
    private List<String> imageNames;

    @NotNull
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
    private List<Price> prices;
    private Integer personPrice;

}
