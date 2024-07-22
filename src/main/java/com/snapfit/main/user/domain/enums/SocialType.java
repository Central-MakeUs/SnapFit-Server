package com.snapfit.main.user.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.snapfit.main.common.exception.ErrorResponse;
import com.snapfit.main.user.domain.exception.UserErrorCode;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SocialType {
    KAKAO("kakao"),
    APPLE("apple")
    ;

    @JsonValue
    private final String socialName;

    SocialType(String socialName) {
        this.socialName = socialName;
    }


    public static SocialType findBySocial(String social) {
        return Arrays.stream(SocialType.values()).filter(socialType -> socialType.socialName.equalsIgnoreCase(social))
                .findFirst()
                .orElseThrow(() ->new ErrorResponse(UserErrorCode.INVALID_REQUEST));
    }

}
