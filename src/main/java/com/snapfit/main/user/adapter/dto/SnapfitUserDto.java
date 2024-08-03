package com.snapfit.main.user.adapter.dto;

import com.snapfit.main.user.domain.SnapfitUser;
import com.snapfit.main.user.domain.Vibe;
import com.snapfit.main.user.domain.enums.SocialType;
import lombok.Getter;

import java.util.List;


@Getter
public class SnapfitUserDto {
    private final Long id;
    private final String nickName;

    private final List<Vibe> vibes;

    private final SocialType socialType;

    private final boolean isMarketingReceive;
    private final boolean isPhotographer;
    private final boolean isNoti;

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
