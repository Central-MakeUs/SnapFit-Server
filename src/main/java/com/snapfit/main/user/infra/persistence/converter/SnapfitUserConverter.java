package com.snapfit.main.user.infra.persistence.converter;

import com.snapfit.main.user.domain.SnapfitUser;
import com.snapfit.main.common.domain.vibe.Vibe;
import com.snapfit.main.user.domain.enums.SocialType;
import io.r2dbc.spi.Row;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ReadingConverter
public class SnapfitUserConverter implements Converter<Row, SnapfitUser> {
    @Override
    public SnapfitUser convert(Row source) {
        System.out.println(source);
        Long userId = source.get("id", Long.class);
        String nickName = source.get("nick_name", String.class);
        SocialType socialType = SocialType.findBySocial(source.get("social_type", String.class));
        String socialId = source.get("social_id", String.class);
        LocalDateTime createdAt = source.get("created_at", LocalDateTime.class);
        LocalDateTime loginTime = source.get("login_time", LocalDateTime.class);
        boolean isMarketingReceive = source.get("is_marketing_receive", Boolean.class);
        boolean isPhotographer = source.get("is_photographer", Boolean.class);
        boolean isNoti = source.get("is_noti", Boolean.class);
        boolean isValid = source.get("is_valid", Boolean.class);
        String profilePath = source.get("profile", String.class);

        List<Vibe> vibes = new ArrayList<>();
        Long[] vibeIds = source.get("vibe_ids", Long[].class);
        String[] vibeNames = source.get("vibe_names", String[].class);

        if (vibeIds != null && vibeNames != null) {
            for (int i = 0; i < vibeIds.length; i++) {
                vibes.add(new Vibe(vibeIds[i], vibeNames[i]));
            }
        }

        return SnapfitUser.builder()
                .id(userId)
                .nickName(nickName)
                .socialType(socialType)
                .socialId(socialId)
                .createdAt(createdAt)
                .loginTime(loginTime)
                .isMarketingReceive(isMarketingReceive)
                .isPhotographer(isPhotographer)
                .isNoti(isNoti)
                .isValid(isValid)
                .profilePath(profilePath)
                .vibes(vibes)
                .build();

    }

}
