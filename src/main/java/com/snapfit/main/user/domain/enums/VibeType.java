package com.snapfit.main.user.domain.enums;

import com.snapfit.main.common.exception.ErrorResponse;
import com.snapfit.main.common.exception.enums.CommonErrorCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum VibeType {

    TEST("테스트");

    private final String vibe;

    private static final Map<String, VibeType> VIBE_TYPE_MAP = new HashMap<>();

    static {
        for (VibeType vibeType : values()) {
            VIBE_TYPE_MAP.put(vibeType.vibe, vibeType);
        }
    }



    VibeType(String vibe) {
        this.vibe = vibe;
    }

    public static VibeType findByVibe(String vibe) {
        VibeType mappingVibe = VIBE_TYPE_MAP.get(vibe);

        if (mappingVibe == null) {
            throw new ErrorResponse(CommonErrorCode.INVALID_REQUEST);
        }

        return mappingVibe;
    }



}
