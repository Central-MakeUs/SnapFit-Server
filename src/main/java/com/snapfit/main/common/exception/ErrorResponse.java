package com.snapfit.main.common.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ErrorResponse extends RuntimeException{
    private final ErrorCode errorCode;

    @Override
    public String getMessage() {
        return "";
    }
}
