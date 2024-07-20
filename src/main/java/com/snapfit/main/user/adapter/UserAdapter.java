package com.snapfit.main.user.adapter;

import com.snapfit.main.common.annoataion.Adapter;
import com.snapfit.main.security.JwtToken;
import com.snapfit.main.security.JwtTokenProvider;
import com.snapfit.main.user.application.UserService;
import com.snapfit.main.user.presentation.dto.SignUpDto;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Adapter
@RequiredArgsConstructor
public class UserAdapter {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

//    public Mono<JwtToken> login() {
//
//    }

    public Mono<JwtToken> signUp(String socialAccessToken, SignUpDto signUpDto) {
        return userService.signUp(socialAccessToken, signUpDto, jwtTokenProvider);
    }
}
