package com.snapfit.main.user.domain;

import reactor.core.publisher.Mono;

public interface SocialLogin {
    Mono<? extends SocialInfo> getSocialInfo(String token);
}
