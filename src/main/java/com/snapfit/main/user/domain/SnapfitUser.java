package com.snapfit.main.user.domain;

import com.snapfit.main.common.domain.vibe.Vibe;
import com.snapfit.main.user.domain.enums.SocialType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@Table("snapfit_user")
@Builder
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class SnapfitUser {
    @Id
    //TODO r2dbc는 generatedValue 지원하지 않음. table 만들 때 id bigserial NOT NULL 필요!
    private Long id;

    private String nickName;

    @Transient
    private List<Vibe> vibes;

    @Column("social_type")
    private SocialType socialType;

    @Column("social_id")
    private String socialId;

    @CreatedDate
    private LocalDateTime createdAt;
    private LocalDateTime loginTime;

//    private String loginDevice;

    @Column("is_marketing_receive")
    private boolean isMarketingReceive;

    @Column("is_photographer")
    private boolean isPhotographer;

    @Column("is_noti")
    private boolean isNoti;

    @Column("is_valid")
    private boolean isValid;

    @Column("profile")
    private String profilePath;


    public void updateLoginTime() {
        loginTime = LocalDateTime.now();
    }

    public void leaveSnapfit() {
        isValid = false;
    }

    public void updateInfo(List<Vibe> vibes, String nickName) {
        this.vibes = vibes;
        this.nickName = nickName;
    }

}
