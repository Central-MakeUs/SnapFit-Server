package com.snapfit.main.user.domain;

import com.snapfit.main.user.domain.enums.SocialType;
import com.snapfit.main.user.domain.enums.VibeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@Table("SnapfitUser")
@Builder
@Getter
@AllArgsConstructor
public class SnapfitUser {
    @Id
    //TODO r2dbc는 generatedValue 지원하지 않음. table 만들 때 id bigserial NOT NULL 필요!
    private Long id;

    private String nickName;

    private List<VibeType> vibes;

    @Column("social_type")
    private SocialType socialType;

    @Column("social_id")
    private String socialId;

    @CreatedDate
    private LocalDateTime createdAt;
    private LocalDateTime loginTime;

//    private String loginDevice;

    private boolean is_marketing_receive;
    private boolean is_photographer;
    private boolean is_noti;


}
