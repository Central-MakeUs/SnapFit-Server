package com.snapfit.main.user.adapter.dto;

import com.snapfit.main.user.domain.SnapfitUser;
import com.snapfit.main.common.domain.vibe.Vibe;
import com.snapfit.main.user.domain.enums.SocialType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;


@Getter
public class SnapfitUserDto {
    private final Long id;
    private final String nickName;

    private final List<Vibe> vibes;

    private final SocialType socialType;

    private final boolean isMarketingReceive;

    @Schema(description = "메이커 여부")
    private final boolean isPhotographer;
    private final boolean isNoti;

    @Schema(description = "프로필 사진. null 인 경우 없음")
    private final String profile;

    public SnapfitUserDto(SnapfitUser snapfitUser) {
        this.id = snapfitUser.getId();
        this.nickName = snapfitUser.getNickName();

        this.vibes = snapfitUser.getVibes();

        this.socialType = snapfitUser.getSocialType();

        this.isMarketingReceive = snapfitUser.isMarketingReceive();
        this.isPhotographer = snapfitUser.isPhotographer();
        this.isNoti = snapfitUser.isNoti();
        this.profile = snapfitUser.getProfilePath();
    }
}
