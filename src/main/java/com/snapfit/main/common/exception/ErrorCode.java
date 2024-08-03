package com.snapfit.main.common.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.snapfit.main.common.exception.enums.CommonErrorCode;
import com.snapfit.main.user.domain.exception.UserErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

@JsonSerialize(as=ErrorCode.class)
public interface ErrorCode {
    @JsonProperty("errorCode")
    int getErrorCode();

    @JsonProperty("message")
    String getMessage();

    @JsonIgnore
    HttpStatus getHttpStatus();
}
