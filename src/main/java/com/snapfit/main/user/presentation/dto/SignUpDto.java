package com.snapfit.main.user.presentation.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.snapfit.main.user.domain.enums.DeviceType;
import com.snapfit.main.user.domain.enums.SocialType;
import com.snapfit.main.user.domain.enums.VibeType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class SignUpDto {
    private SocialType social;
    private List<VibeType> vibes;
    private DeviceType device_type;
    private String fcm_token;
    private boolean is_marketing;
    private String nickName;

    @JsonCreator
    public static SocialType findSocialType(String social) {
        return SocialType.findBySocial(social);
    }

    @JsonCreator
    public static VibeType findVibe(String vibe) {
        return VibeType.findByVibe(vibe);
    }

    @JsonCreator
    public static DeviceType deviceType(String deviceType) {
        return DeviceType.findByDevice(deviceType);
    }


}
