package com.snapfit.main.user.presentation.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.snapfit.main.user.domain.enums.DeviceType;
import com.snapfit.main.user.domain.enums.SocialType;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class SignUpDto {
    private SocialType social;

    @Size(min = 1, max = 2)
    private List<String> vibes;

    private DeviceType deviceType;
    private String deviceToken;
    private boolean isMarketing;

    @Size(min = 2, max = 8)
    private String nickName;

    @JsonCreator
    public static SocialType findSocialType(String social) {
        return SocialType.findBySocial(social);
    }

    @JsonCreator
    public static DeviceType deviceType(String deviceType) {
        return DeviceType.findByDevice(deviceType);
    }


}
