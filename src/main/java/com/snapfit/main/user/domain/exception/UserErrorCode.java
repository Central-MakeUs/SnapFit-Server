package com.snapfit.main.user.domain.exception;

import com.snapfit.main.common.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

@Schema(implementation = ErrorCode.class, description = "login-controller 에서 반환하는 응답 값")
public enum UserErrorCode implements ErrorCode {
    //범위 10 ~ 20
    INVALID_SOCIAL_TOKEN(HttpStatus.UNAUTHORIZED, 10, "소셜 로그인 토큰이 만료되었거나 불안정합니다."),
    NOT_EXIST_USER(HttpStatus.NOT_FOUND, 11, "유저가 존재하지 않습니다."),
    EXIST_USER(HttpStatus.CONFLICT, 13, "유저가 존재합니다."),
    LEAVE_USER(HttpStatus.NOT_FOUND, 14, "탈퇴한 유저입니다."),
    EXIST_NICKNAME(HttpStatus.CONFLICT, 15, "이미 사용 중인 닉네임입니다."),
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
