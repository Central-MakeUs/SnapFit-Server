package com.snapfit.main.post.domain.exception;

import com.snapfit.main.common.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

@Schema(implementation = ErrorCode.class, description = "post-controller 에서 반환하는 응답 값")
public enum PostErrorCode implements ErrorCode {
    //범위 21 30
    NOT_MAKER(HttpStatus.FORBIDDEN, 21, "메이커가 아닙니다."),
    NOT_EXIST_IMAGE(HttpStatus.NOT_FOUND, 22, "이미지 파일이 존재하지 않습니다."),
    NOT_EXIST_POST(HttpStatus.NOT_FOUND, 23, "포스트가 존재하지 않습니다."),
    ALREADY_LIKE_POST(HttpStatus.CONFLICT, 24, "이미 찜했거나 찜하지 않은 상품입니다.")
    ;


    private final HttpStatus httpStatus;
    private final int errorCode;
    private final String message;

    PostErrorCode(HttpStatus httpStatus, int errorCode, String message) {
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
