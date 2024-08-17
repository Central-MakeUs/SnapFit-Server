package com.snapfit.main.user.presentation.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class InfoModifyDto {
    @Size(min=2, max=8)
    private String nickName;
    @Size(min=1, max=2)
    private List<String> vibes;
}
