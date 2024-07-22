package com.snapfit.main.user.adapter.dto;

import com.snapfit.main.user.domain.SnapfitUser;
import com.snapfit.main.user.domain.enums.SocialType;
import com.snapfit.main.user.domain.enums.VibeType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.List;


@Getter
public class SnapfitUserDto {
    private final Long id;
    private final String nickName;

    private final List<VibeType> vibes;

    private final SocialType socialType;

    private final boolean is_marketing_receive;
    private final boolean is_photographer;
    private final boolean is_noti;

    public SnapfitUserDto(SnapfitUser snapfitUser) {
        this.id = snapfitUser.getId();
        this.nickName = snapfitUser.getNickName();

        this.vibes = snapfitUser.getVibes();

        this.socialType = snapfitUser.getSocialType();

        this.is_marketing_receive = snapfitUser.isMarketingReceive();
        this.is_photographer = snapfitUser.isPhotographer();
        this.is_noti = snapfitUser.isNoti();
    }
}
