package com.snapfit.main.security;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("refresh_token")
public class RefreshTokenInfo {

    @Id
    private Long id;

    @Column("refresh_token")
    private String refreshToken;

    //fk
    @Column("user_id")
    private Long userId;

    @Column("is_logout")
    private boolean isLogout;
}
