package com.snapfit.main.common.exception.enums;

import com.snapfit.main.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum CommonErrorCode implements ErrorCode {
    //0~9
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, 1, "올바르지 않은 요청입니다."),
    INVALID_AUTH(HttpStatus.UNAUTHORIZED, 2, "권한이 없습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED,3, "토큰이 만료되었거나, 올바르지 않습니다."),
    VALID_RESERVATION_EXISTS(HttpStatus.UNAUTHORIZED,4, "기예약 건이 존재하여 탈퇴할 수 없습니다.")
    ;

    private final HttpStatus httpStatus;
    private final int errorCode;
    private final String message;

    CommonErrorCode(HttpStatus httpStatus, int errorCode, String message) {
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
