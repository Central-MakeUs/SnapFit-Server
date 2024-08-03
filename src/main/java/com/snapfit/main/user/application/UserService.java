package com.snapfit.main.user.application;

import com.snapfit.main.common.exception.ErrorResponse;
import com.snapfit.main.common.exception.enums.CommonErrorCode;
import com.snapfit.main.security.JwtToken;
import com.snapfit.main.security.JwtTokenProvider;
import com.snapfit.main.security.dto.RequestTokenInfo;
import com.snapfit.main.user.domain.*;
import com.snapfit.main.user.domain.enums.DeviceType;
import com.snapfit.main.user.domain.enums.SocialType;
import com.snapfit.main.user.domain.VibeFinder;
import com.snapfit.main.user.domain.exception.UserErrorCode;
import com.snapfit.main.user.presentation.dto.SignUpDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final SnapfitUserRepository snapfitUserRepository;
    private final DeviceRepository deviceRepository;
    private final UserVibeRepository userVibeRepository;
    private final Map<SocialType, SocialLogin> socialLoginMap;
    private final VibeFinder vibeFinder;

    @Transactional
    public Mono<JwtToken> signUp(String accessToken, SignUpDto signUpDto, JwtTokenProvider jwtTokenProvider) {
        SocialType requestSocialType = signUpDto.getSocial();

        if (socialLoginMap.get(requestSocialType) == null) {
            return Mono.error(new ErrorResponse(CommonErrorCode.INVALID_REQUEST));
        }

        return socialLoginMap.get(requestSocialType).getSocialInfo(accessToken)
                // 1. 해당 소셜의 계정이 이미 가입되어 있는 지 확인한다. 가입되어 있다면 에러 발생
                .flatMap(socialInfo -> isExistUser(requestSocialType, socialInfo.getSocialId(), signUpDto.getNickName()).then(Mono.just(socialInfo)))
                // 2. 유저 정보 db에 저장
                .flatMap(socialInfo -> {
                    SnapfitUser user = SnapfitUser.builder()
                            .socialType(requestSocialType)
                            .socialId(socialInfo.getSocialId())
//                            .vibes(signUpDto.getVibes().stream().map(vibeType::findByVibe).toList())
                            .isNoti(true)
                            .isPhotographer(false)
                            .nickName(signUpDto.getNickName())
                            .loginTime(LocalDateTime.now())
                            .isValid(true)
                            .profilePath(null)
                            .build();

                    List<Vibe> vibes = signUpDto.getVibes().stream().map(vibeFinder::findByVibe).toList();

                    return snapfitUserRepository.save(user)
                            .flatMap(saveUser -> {
                                List<UserVibe> userVibes = vibes.stream().map(vibe -> UserVibe.builder()
                                        .userId(saveUser.getId())
                                        .vibeId(vibe.getId())
                                        .build()).toList();

                                return userVibeRepository.saveAll(userVibes)
                                        .then(Mono.just(saveUser));

                            });
                })
                //3. device 정보 db에 저장
                .flatMap(user -> upsertDevice(user, signUpDto.getDeviceType(), signUpDto.getDeviceToken())
                        .then(Mono.just(user)))
                // 4. 토큰 반환
                .map(snapfitUser -> jwtTokenProvider.createToken(new RequestTokenInfo(snapfitUser)));
    }

    @Transactional
    public Mono<SnapfitUser> getSnapfitUser(String socialToken, SocialType socialType) {
        if (socialLoginMap.get(socialType) ==  null) {
            return Mono.error(new ErrorResponse(CommonErrorCode.INVALID_REQUEST));
        }

        return socialLoginMap.get(socialType).getSocialInfo(socialToken)
                .flatMap(socialInfo -> snapfitUserRepository.findBySocialIdAndSocialType(socialInfo.getSocialId(), socialType))
                .switchIfEmpty(Mono.error(new ErrorResponse(UserErrorCode.NOT_EXIST_USER)))
                .filter(SnapfitUser::isValid)
                .switchIfEmpty(Mono.error(new ErrorResponse(UserErrorCode.LEAVE_USER)));
    }

    @Transactional
    public Mono<SnapfitUser> login(String socialToken, SocialType socialType) {
        if (socialLoginMap.get(socialType) ==  null) {
            return Mono.error(new ErrorResponse(CommonErrorCode.INVALID_REQUEST));
        }

        return getSnapfitUser(socialToken, socialType)
                .flatMap(this::updateLoginTime);
    }

    @Transactional(readOnly = true)
    public Mono<List<Vibe>> findAllVibes() {
        return Mono.just(vibeFinder.findAllVibes());
    }

    private Mono<SnapfitUser> updateLoginTime(SnapfitUser snapfitUser) {
        return Mono.just(snapfitUser)
                .flatMap(user -> {
                    user.updateLoginTime();
                    return snapfitUserRepository.save(user);
                });
    }

    @Transactional(readOnly = true)
    public Mono<SnapfitUser> getSnapfitUser(long userId) {
        return snapfitUserRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ErrorResponse(UserErrorCode.NOT_EXIST_USER)));
    }

    @Transactional
    public Mono<Void> leaveSnapfit(long userId) {
        return snapfitUserRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ErrorResponse(UserErrorCode.NOT_EXIST_USER)))
                .flatMap(snapfitUser -> {
                    snapfitUser.leaveSnapfit();
                    return snapfitUserRepository.save(snapfitUser).then(Mono.empty());
                });
    }

    @Transactional(readOnly = true)
    public Mono<Void> assertValidUser(long userId) {
        return snapfitUserRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ErrorResponse(UserErrorCode.NOT_EXIST_USER)))
                .filter(SnapfitUser::isValid)
                .switchIfEmpty(Mono.error(new ErrorResponse(UserErrorCode.LEAVE_USER)))
                .then();
    }



    private Mono<Void> isExistUser(SocialType socialType, String socialId, String nickName) {
        return snapfitUserRepository.existsByNickName(nickName)
                .flatMap(isExist -> isExist ? Mono.error(new ErrorResponse(UserErrorCode.EXIST_USER)) : snapfitUserRepository.existsBySocialIdAndSocialType(socialId, socialType))
                .flatMap(isExist -> isExist ? Mono.error(new ErrorResponse(UserErrorCode.EXIST_USER)) : Mono.empty());
    }

    private Mono<Device> upsertDevice(SnapfitUser user, DeviceType deviceType, String deviceId) {
        return deviceRepository.findByDeviceId(deviceId)
                .switchIfEmpty(Mono.just(Device.builder()
                        .userId(user.getId())
                        .deviceId(deviceId)
                        .deviceType(deviceType)
                        .loginDateTime(LocalDateTime.now())
                        .build()))
                .flatMap(deviceRepository::save);
    }
}
