package com.snapfit.main.user.infra.persistence;

import com.snapfit.main.common.exception.ErrorResponse;
import com.snapfit.main.user.domain.SnapfitUser;
import com.snapfit.main.user.domain.SnapfitUserRepository;
import com.snapfit.main.user.domain.UserVibeRepository;
import com.snapfit.main.user.domain.Vibe;
import com.snapfit.main.user.domain.enums.SocialType;
import com.snapfit.main.user.domain.exception.UserErrorCode;
import com.snapfit.main.user.infra.persistence.r2dbc.SnapfitUserR2dbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SnapfitUserRepositoryImpl implements SnapfitUserRepository {

    private final SnapfitUserR2dbcRepository snapfitUserR2dbcRepository;
    private final DatabaseClient databaseClient;
    private final UserVibeRepository userVibeRepository;

    @Override
    public Mono<SnapfitUser> findBySocialIdAndSocialType(String socialId, SocialType socialType) {
        return databaseClient.sql("SELECT su.*, uv.vibe_id, v.name as vibe_name FROM SnapfitUser su " +
                "LEFT JOIN UserVibe uv ON su.id = uv.user_id " +
                "LEFT JOIN Vibe v ON uv.vibe_id = v.id " +
                "WHERE su.social_id = :socialId AND su.social_type = :socialType")
                .bind("socialId", socialId)
                .bind("socialType", socialType.getSocialName())
                .fetch()
                .all()
                .collectList()
                .map(this::mapResultsToSnapfitUser);
    }

    @Override
    public Mono<Boolean> existsBySocialIdAndSocialType(String socialId, SocialType socialType) {
        return snapfitUserR2dbcRepository.existsBySocialIdAndSocialType(socialId, socialType);
    }

    @Override
    public Mono<Boolean> existsByNickName(String nickName) {
        return snapfitUserR2dbcRepository.existsByNickName(nickName);
    }

    @Override
    public Mono<SnapfitUser> findById(Long id) {
        return databaseClient.sql("SELECT su.*, uv.vibe_id, v.name as vibe_name FROM SnapfitUser su " +
                        "LEFT JOIN UserVibe uv ON su.id = uv.user_id " +
                        "LEFT JOIN Vibe v ON uv.vibe_id = v.id " +
                        "WHERE su.id = :id")
                .bind("id", id)
                .fetch()
                .all()
                .collectList()
                .map(this::mapResultsToSnapfitUser);
    }

    @Override
    public Mono<SnapfitUser> save(SnapfitUser snapfitUser) {
        return snapfitUserR2dbcRepository.save(snapfitUser);
    }

    private SnapfitUser mapResultsToSnapfitUser(List<Map<String, Object>> results) {
        if (results.isEmpty()) {
            throw new ErrorResponse(UserErrorCode.NOT_EXIST_USER);
        }

        Map<String, Object> firstResult = results.get(0);
        SnapfitUser snapfitUser = SnapfitUser.builder()
                .id((Long) firstResult.get("id"))
                .nickName((String) firstResult.get("nick_name"))
                .socialType(SocialType.findBySocial((String) firstResult.get("social_type")))
                .socialId((String) firstResult.get("social_id"))
                .createdAt((LocalDateTime) firstResult.get("created_at"))
                .loginTime((LocalDateTime) firstResult.get("login_time"))
                .isMarketingReceive((Boolean) firstResult.get("is_marketing_receive"))
                .isPhotographer((Boolean) firstResult.get("is_photographer"))
                .isNoti((Boolean) firstResult.get("is_noti"))
                .isValid((Boolean) firstResult.get("is_valid"))
                .profilePath((String) firstResult.get("profile"))
                .vibes(results.stream()
                        .map(result -> new Vibe((Long) result.get("vibe_id"), (String) result.get("vibe_name")))
                        .collect(Collectors.toList()))
                .build();

        return snapfitUser;
    }
}
