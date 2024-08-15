package com.snapfit.main.reservation.domain.exception;

import com.snapfit.main.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ReservationErrorCode implements ErrorCode {
    //31 ~ 40
    INVALID_PRICE(HttpStatus.UNPROCESSABLE_ENTITY, 31, "올바르지 않은 가격입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, 32, "존재하지 않는 예약입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, 33, "권한이 없습니다.");
    private final HttpStatus httpStatus;
    private final int errorCode;
    private final String message;

    ReservationErrorCode(HttpStatus httpStatus, int errorCode, String message) {
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
