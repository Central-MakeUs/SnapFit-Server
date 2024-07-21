package com.snapfit.main.security;

import com.snapfit.main.common.exception.ErrorResponse;
import com.snapfit.main.common.exception.enums.CommonErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class JwtToken {
    private String accessToken;
    private String refreshToken;

    public static String parseAccessTokenFromHeader(String accessToken) {
        if (accessToken == null) {
            throw new ErrorResponse(CommonErrorCode.INVALID_REQUEST);
        }

        String[] parseToken = accessToken.split(" ");

        if (parseToken.length != 2 || !parseToken[0].equals("Bearer")) {
            throw new ErrorResponse(CommonErrorCode.INVALID_REQUEST);
        }

        return parseToken[1];
    }
}
