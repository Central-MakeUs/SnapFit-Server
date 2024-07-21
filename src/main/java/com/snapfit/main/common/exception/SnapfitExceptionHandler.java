package com.snapfit.main.common.exception;


import com.snapfit.main.common.exception.enums.CommonErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class SnapfitExceptionHandler {

    @ExceptionHandler(ErrorResponse.class)
    public Mono<ResponseEntity<ErrorCode>> handleErrorResponse(ErrorResponse errorResponse) {
        return Mono.just(ResponseEntity.status(errorResponse.getErrorCode().getHttpStatus()).body(errorResponse.getErrorCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Mono<ResponseEntity<ErrorCode>> handleErrorResponse(MethodArgumentNotValidException methodArgumentNotValidException) {
        return Mono.just(ResponseEntity.status(CommonErrorCode.INVALID_REQUEST.getHttpStatus()).body(CommonErrorCode.INVALID_REQUEST));
    }

}
