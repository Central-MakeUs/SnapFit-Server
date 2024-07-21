package com.snapfit.main.user.domain.exception;

import com.snapfit.main.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum UserErrorCode implements ErrorCode {
    INVALID_SOCIAL_TOKEN(HttpStatus.UNAUTHORIZED, 1, "소셜 로그인 토큰이 만료되었거나 불안정합니다."),
    NOT_EXIST_USER(HttpStatus.UNAUTHORIZED, 2, "유저가 존재하지 않습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, 3, "올바르지 않은 요청입니다."),
    EXIST_USER(HttpStatus.UNAUTHORIZED, 4, "유저가 존재합니다."),
    ;

    private final HttpStatus httpStatus;
    private final int errorCode;
    private final String message;

    UserErrorCode(HttpStatus httpStatus, int errorCode, String message) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.message = message;
    }


    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
