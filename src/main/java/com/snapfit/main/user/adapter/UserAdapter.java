package com.snapfit.main.user.adapter;

import com.snapfit.main.common.annoataion.Adapter;
import com.snapfit.main.common.exception.ErrorResponse;
import com.snapfit.main.security.JwtToken;
import com.snapfit.main.security.JwtTokenProvider;
import com.snapfit.main.security.RefreshTokenInfo;
import com.snapfit.main.security.dto.RequestTokenInfo;
import com.snapfit.main.user.adapter.dto.SnapfitUserDto;
import com.snapfit.main.user.application.UserService;
import com.snapfit.main.user.domain.SnapfitUser;
import com.snapfit.main.user.domain.enums.SocialType;
import com.snapfit.main.user.domain.exception.UserErrorCode;
import com.snapfit.main.user.presentation.dto.SignUpDto;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Adapter
@RequiredArgsConstructor
public class UserAdapter {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public Mono<JwtToken> signUp(String socialAccessToken, SignUpDto signUpDto) {
        return userService.signUp(socialAccessToken, signUpDto, jwtTokenProvider);
    }

    public Mono<JwtToken> login(String socialAccessToken, SocialType socialType) {

        return userService.login(socialAccessToken, socialType)
                .map(snapfitUser -> jwtTokenProvider.createToken(new RequestTokenInfo(snapfitUser)));
    }

    public Mono<SnapfitUserDto> getSnapfitUser(long userId) {
        return userService.getSnapfitUser(userId)
                .map(SnapfitUserDto::new);
    }

    public Mono<JwtToken> refreshToken(String refreshToken) {
        return jwtTokenProvider.refreshToken(refreshToken);
    }

    public Mono<Void> logOut(Long userId, String refreshToken) {
        return jwtTokenProvider.logOut(userId, refreshToken);
    }

    public Mono<Void> leaveSnapfit(Long userId) {
        return userService.leaveSnapfit(userId);
    }
}
