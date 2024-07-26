package com.snapfit.main.user.presentation.dto;

import lombok.Data;

@Data
public class RequestRefreshToken {
    private String refreshToken;
    private Long userId;
}
