package com.snapfit.main.user.presentation.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class InfoModifyDto {
    private String nickName;
    private List<String> vibes;
}
