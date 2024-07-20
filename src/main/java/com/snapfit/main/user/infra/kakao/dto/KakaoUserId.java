package com.snapfit.main.user.infra.kakao.dto;

import com.snapfit.main.user.domain.SocialInfo;
import lombok.Data;

@Data
public class KakaoUserId implements SocialInfo {
    private String id;


    @Override
    public String getSocialId() {
        return id;
    }
}
