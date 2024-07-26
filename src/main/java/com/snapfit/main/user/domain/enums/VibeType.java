package com.snapfit.main.user.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.snapfit.main.common.exception.ErrorResponse;
import com.snapfit.main.common.exception.enums.CommonErrorCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum VibeType {

    //TODO 나중에 db에 설정할 수 있도록 수정.
    LOVELY("러블리"),
    CHIC("시크"),
    KITSCH("키치"),
    CALM("차분함"),
    TEEN("하이틴"),
    VINTAGE("빈티지"),
    DREAMY("몽환적"),
    BRIGHT("밝은");

    @JsonValue
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
