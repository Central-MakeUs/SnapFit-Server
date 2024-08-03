package com.snapfit.main.user.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("UserVibe")
@Builder
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class UserVibe {
    @Id
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("vibe_id")
    private Long vibeId;
}
