package com.snapfit.main.config;

import com.snapfit.main.user.domain.SocialLogin;
import com.snapfit.main.user.domain.enums.SocialType;
import com.snapfit.main.user.infra.kakao.KakaoLogin;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.EnumMap;
import java.util.Map;

@Configuration
public class SocialConfig {

    @Value("${social.kakao.url")
    private String KAKAO_URL;

    @Value("${social.apple.get_user_id_url")
    private String APPLE_GET_USER_ID_URL;

    @Bean
    @Qualifier("kakaoWebClient")
    public WebClient kakaoWebClient() {
        System.out.println(KAKAO_URL);
        return WebClient.builder()
                .baseUrl("https://kapi.kakao.com/v2/user/me")
                .build();
    }

    @Bean
    @Qualifier("appleWebClient")
    public WebClient appleWebClient() {
        return WebClient.builder()
                .baseUrl(APPLE_GET_USER_ID_URL)
                .build();
    }

    @Bean
    public Map<SocialType, SocialLogin> socialLoginMap(KakaoLogin kakaoLogin) {
        Map<SocialType, SocialLogin> map = new EnumMap<>(SocialType.class);
        map.put(SocialType.KAKAO, kakaoLogin);

        return map;
    }
}
