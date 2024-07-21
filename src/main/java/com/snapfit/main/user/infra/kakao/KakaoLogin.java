package com.snapfit.main.user.infra.kakao;

import com.snapfit.main.common.exception.ErrorResponse;
import com.snapfit.main.user.domain.SocialInfo;
import com.snapfit.main.user.domain.SocialLogin;
import com.snapfit.main.user.domain.exception.UserErrorCode;
import com.snapfit.main.user.infra.kakao.dto.KakaoUserId;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Component
public class KakaoLogin implements SocialLogin {


    private final WebClient kakaoWebClient;

    public KakaoLogin(@Qualifier("kakaoWebClient") WebClient kakaoWebClient) {
        this.kakaoWebClient = kakaoWebClient;
    }

    @Override
    public Mono<? extends SocialInfo> getSocialInfo(String token) {
        return kakaoWebClient.get()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "Content-type: application/x-www-form-urlencoded;charset=utf-8")
                .retrieve()
                .onStatus(httpStatusCode -> !httpStatusCode.is2xxSuccessful(), clientResponse -> Mono.error(new ErrorResponse(UserErrorCode.INVALID_SOCIAL_TOKEN)))
                .bodyToMono(KakaoUserId.class)
                .log();
    }


}
