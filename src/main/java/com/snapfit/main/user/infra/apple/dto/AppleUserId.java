package com.snapfit.main.user.infra.apple.dto;

import com.snapfit.main.user.domain.SocialInfo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppleUserId implements SocialInfo {
    private String id;

    @Override
    public String getSocialId() {
        return id;
    }
}
