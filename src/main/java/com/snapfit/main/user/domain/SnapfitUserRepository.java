package com.snapfit.main.user.domain;

import com.snapfit.main.user.domain.enums.SocialType;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import reactor.core.publisher.Mono;


//TODO 나머지 r2dbc 전부 infra 계층으로 내리고, 이 안에 넣을 필요 있어보임?
@EnableR2dbcRepositories
public interface SnapfitUserRepository {

    Mono<SnapfitUser> findBySocialIdAndSocialType(String socialId, SocialType socialType);

    Mono<Boolean> existsBySocialIdAndSocialType(String socialId, SocialType socialType);

    Mono<Boolean> existsByNickName(String nickName);

    Mono<SnapfitUser> findById(Long id);

    Mono<SnapfitUser> save(SnapfitUser snapfitUser);
}
