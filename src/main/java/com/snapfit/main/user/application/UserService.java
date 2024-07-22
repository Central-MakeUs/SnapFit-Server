package com.snapfit.main.user.application;

import com.snapfit.main.common.exception.ErrorResponse;
import com.snapfit.main.common.exception.enums.CommonErrorCode;
import com.snapfit.main.security.JwtToken;
import com.snapfit.main.security.JwtTokenProvider;
import com.snapfit.main.security.dto.RequestTokenInfo;
import com.snapfit.main.user.adapter.dto.SnapfitUserDto;
import com.snapfit.main.user.domain.*;
import com.snapfit.main.user.domain.enums.DeviceType;
import com.snapfit.main.user.domain.enums.SocialType;
import com.snapfit.main.user.domain.exception.UserErrorCode;
import com.snapfit.main.user.presentation.dto.SignUpDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final SnapfitUserRepository snapfitUserRepository;
    private final DeviceRepository deviceRepository;
    private final Map<SocialType, SocialLogin> socialLoginMap;

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
                            .vibes(signUpDto.getVibes())
                            .is_noti(true)
                            .is_photographer(false)
                            .nickName(signUpDto.getNickName())
                            .loginTime(LocalDateTime.now())
                            .build();

                    return snapfitUserRepository.save(user);
                })
                //3. device 정보 db에 저장
                .flatMap(user -> upsertDevice(user, signUpDto.getDevice_type(), signUpDto.getFcm_token())
                        .then(Mono.just(user)))
                // 4. 토큰 반환
                .flatMap(snapfitUser -> Mono.just(jwtTokenProvider.createToken(new RequestTokenInfo(snapfitUser))));
    }

    @Transactional
    public Mono<SnapfitUser> getSnapfitUser(String socialToken, SocialType socialType) {
        if (socialLoginMap.get(socialType) ==  null) {
            return Mono.error(new ErrorResponse(CommonErrorCode.INVALID_REQUEST));
        }

        return socialLoginMap.get(socialType).getSocialInfo(socialToken)
                .flatMap(socialInfo -> snapfitUserRepository.findBySocialIdAndSocialType(socialInfo.getSocialId(), socialType))
                .switchIfEmpty(Mono.error(new ErrorResponse(UserErrorCode.NOT_EXIST_USER)));
    }

    @Transactional
    public Mono<SnapfitUser> getSnapfitUser(long userId) {
        return snapfitUserRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ErrorResponse(UserErrorCode.NOT_EXIST_USER)));
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
