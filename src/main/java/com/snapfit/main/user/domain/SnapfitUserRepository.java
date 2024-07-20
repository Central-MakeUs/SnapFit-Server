package com.snapfit.main.user.domain;

import com.snapfit.main.user.domain.enums.SocialType;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import reactor.core.publisher.Mono;

@EnableR2dbcRepositories
public interface SnapfitUserRepository extends R2dbcRepository<SnapfitUser, Long> {
    Mono<SnapfitUser> findBySocialIdAndSocialType(String socialId, SocialType socialType);

    Mono<Boolean> existsBySocialIdAndSocialType(String socialId, SocialType socialType);

    Mono<Boolean> existsByNickName(String nickName);
}
