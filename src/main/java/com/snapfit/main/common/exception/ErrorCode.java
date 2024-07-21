package com.snapfit.main.common.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.HttpStatus;

public interface ErrorCode {
    int getErrorCode();
    String getMessage();

    @JsonIgnore
    HttpStatus getHttpStatus();
}
