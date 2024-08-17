package com.snapfit.main.common.exception;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.snapfit.main.common.exception.enums.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class SnapfitExceptionHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(ErrorResponse.class)
    public Mono<ResponseEntity<ErrorCode>> handleErrorResponse(ErrorResponse errorResponse) {
        return Mono.just(ResponseEntity.status(errorResponse.getErrorCode().getHttpStatus()).body(errorResponse.getErrorCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Mono<ResponseEntity<ErrorCode>> handleErrorResponse(MethodArgumentNotValidException methodArgumentNotValidException) {
        log.error("{}", methodArgumentNotValidException.getMessage());
        return Mono.just(ResponseEntity.status(CommonErrorCode.INVALID_REQUEST.getHttpStatus()).body(CommonErrorCode.INVALID_REQUEST));
    }

    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<ErrorCode>> handleErrorResponse(ServerWebInputException serverWebInputException) {
        log.error("[INVALID_REQUEST] {}", serverWebInputException.getMessage());
        return Mono.just(ResponseEntity.status(CommonErrorCode.INVALID_REQUEST.getHttpStatus()).body(CommonErrorCode.INVALID_REQUEST));
    }

}
