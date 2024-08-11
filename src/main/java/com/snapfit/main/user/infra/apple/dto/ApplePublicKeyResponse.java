package com.snapfit.main.user.infra.apple.dto;

import com.snapfit.main.common.exception.ErrorResponse;
import com.snapfit.main.user.domain.exception.UserErrorCode;

import java.util.List;

public record ApplePublicKeyResponse(List<ApplePublicKey> keys) {

    public ApplePublicKey getMatchedKey(String kid, String alg) {
        return keys.stream()
                .filter(key -> key.kid().equals(kid) && key.alg().equals(alg))
                .findAny()
                .orElseThrow(() -> new ErrorResponse(UserErrorCode.INVALID_SOCIAL_TOKEN));
    }
}