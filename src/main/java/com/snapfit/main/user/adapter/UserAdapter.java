package com.snapfit.main.user.adapter;

import static com.snapfit.main.common.exception.enums.CommonErrorCode.*;

import com.snapfit.main.common.annoataion.Adapter;
import com.snapfit.main.common.domain.location.Location;
import com.snapfit.main.common.domain.location.LocationFinder;
import com.snapfit.main.common.exception.ErrorResponse;
import com.snapfit.main.reservation.application.ReservationService;
import com.snapfit.main.security.JwtToken;
import com.snapfit.main.security.JwtTokenProvider;
import com.snapfit.main.security.dto.RequestTokenInfo;
import com.snapfit.main.user.adapter.dto.SnapfitUserDto;
import com.snapfit.main.user.application.UserService;
import com.snapfit.main.common.domain.vibe.Vibe;
import com.snapfit.main.user.domain.SnapfitUser;
import com.snapfit.main.user.domain.enums.SocialType;
import com.snapfit.main.user.presentation.dto.InfoModifyDto;
import com.snapfit.main.user.presentation.dto.SignUpDto;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

@Adapter
@RequiredArgsConstructor
public class UserAdapter {
    private final UserService userService;
    private final ReservationService reservationService;
    private final JwtTokenProvider jwtTokenProvider;
    private final LocationFinder locationFinder;

    public Mono<JwtToken> signUp(String socialAccessToken, SignUpDto signUpDto) {
        return userService.signUp(socialAccessToken, signUpDto, jwtTokenProvider);
    }

    public Mono<JwtToken> login(String socialAccessToken, SocialType socialType) {

        return userService.login(socialAccessToken, socialType)
            .flatMap(snapfitUser -> jwtTokenProvider.createToken(new RequestTokenInfo(snapfitUser)));
    }

    public Mono<SnapfitUserDto> modifyUserInfo(long userId, InfoModifyDto request) {
        return userService.modifyUserInfo(userId, request)
            .map(SnapfitUserDto::new);
    }

    public Mono<SnapfitUserDto> getSnapfitUser(long userId) {
        return userService.getSnapfitUser(userId)
            .map(SnapfitUserDto::new);
    }

    public Mono<List<Location>> getLocations() {
        return Mono.just(locationFinder.findAllLocation());
    }

    public Mono<JwtToken> refreshToken(String refreshToken) {
        return jwtTokenProvider.refreshToken(refreshToken, jwtTokenProvider.getUserId(refreshToken));
    }

    public Mono<List<Vibe>> findAllVibes() {
        return userService.findAllVibes();
    }

    public Mono<Void> logOut(Long userId, String refreshToken) {
        return jwtTokenProvider.logOut(userId, refreshToken);
    }

    public Mono<Void> leaveSnapfit(Long userId) {
        return userService.getSnapfitUser(userId)
            .flatMap(user -> user.isPhotographer()
                ? getMakerMono(user)
                : getUserMono(user))
            .then(userService.leaveSnapfit(userId));
    }

    private Mono<SnapfitUser> getMakerMono(SnapfitUser user) {
        return reservationService.findMakerPendingReservation(user.getId())
            .flatMap(getBooleanMonoFunction(user));
    }

    private Mono<SnapfitUser> getUserMono(SnapfitUser user) {
        return reservationService.findUserPendingReservation(user.getId())
            .flatMap(getBooleanMonoFunction(user));
    }

    private Function<Boolean, Mono<? extends SnapfitUser>> getBooleanMonoFunction(SnapfitUser user) {
        return hasPendingReservation -> hasPendingReservation
            ? Mono.error(new ErrorResponse(VALID_RESERVATION_EXISTS))
            : Mono.just(user);
    }

}
