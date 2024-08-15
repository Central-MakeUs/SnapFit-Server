package com.snapfit.main.reservation.adapter;

import com.snapfit.main.common.annoataion.Adapter;
import com.snapfit.main.common.domain.location.Location;
import com.snapfit.main.common.domain.vibe.Vibe;
import com.snapfit.main.common.dto.PageResult;
import com.snapfit.main.common.exception.ErrorResponse;
import com.snapfit.main.post.application.PostService;
import com.snapfit.main.reservation.application.dto.PostSummaryDto;
import com.snapfit.main.post.application.dto.SnapfitUserSummaryDto;
import com.snapfit.main.post.domain.Post;
import com.snapfit.main.post.domain.PostPrice;
import com.snapfit.main.reservation.application.ReservationService;
import com.snapfit.main.reservation.application.dto.ReservationDetailDto;
import com.snapfit.main.reservation.application.dto.ReservationSummaryDto;
import com.snapfit.main.reservation.domain.Reservation;
import com.snapfit.main.reservation.domain.exception.ReservationErrorCode;
import com.snapfit.main.reservation.presentation.dto.ReservationRequest;
import com.snapfit.main.user.application.UserService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Adapter
@RequiredArgsConstructor
public class ReservationAdapter {

    private final ReservationService reservationService;
    private final PostService postService;
    private final UserService userService;


    public Mono<ReservationDetailDto> save(ReservationRequest reservationRequest, long userId) {
        return assertValidReservation(reservationRequest)
                .then(reservationService.save(reservationRequest, userId))
                .flatMap(this::convertToDetailDto);
    }

    public Mono<ReservationDetailDto> cancel(String message, long reservationId, long userId) {
        return reservationService.cancel(message, reservationId, userId)
                .flatMap(this::convertToDetailDto);
    }

    public Mono<PageResult<ReservationSummaryDto>> findByMakerId(int limit, int offset, long makerId) {
        return reservationService.findByMakerId(limit, offset, makerId)
                .flatMap(this::convertToReservationSummary);
    }

    public Mono<PageResult<ReservationSummaryDto>> findByUserId(int limit, int offset, long userId) {
        return reservationService.findByUserId(limit, offset, userId)
                .flatMap(this::convertToReservationSummary);
    }

    public Mono<ReservationDetailDto> findById(long id) {
        return reservationService.findById(id)
                .switchIfEmpty(Mono.error(new ErrorResponse(ReservationErrorCode.NOT_FOUND)))
                .flatMap(this::convertToDetailDto);
    }

    private Mono<PageResult<ReservationSummaryDto>> convertToReservationSummary(PageResult<Reservation> reservations) {
        return Flux.fromIterable(reservations.getData())
                .flatMap(reservation -> {
                    return postService.findPostById(reservation.getPostId())
                            .flatMap(this::convertToPostSummary)
                            .map(postSummaryDto -> ReservationSummaryDto.builder()
                                    .id(reservation.getId())
                                    .post(postSummaryDto)
                                    .reservationTime(reservation.getReservationTime())
                                    .totalPrice(reservation.getMinutesPrice() + reservation.getPersonPrice())
                                    .build());
                }).collectList()
                .map(reservationSummaryDtos -> PageResult.<ReservationSummaryDto>builder()
                        .offset(reservations.getOffset())
                        .limit(reservations.getLimit())
                        .data(reservationSummaryDtos)
                        .build());
    }

    private Mono<ReservationDetailDto> convertToDetailDto(Reservation reservation) {
        return Mono.just(reservation)
                .flatMap(data -> {

                    ReservationDetailDto result = ReservationDetailDto.builder()
                            .id(reservation.getId())
                            .basePrice(reservation.getMinutesPrice())
                            .person(reservation.getPerson())
                            .personPrice(reservation.getPersonPrice())
                            .totalPrice(data.getPersonPrice() + data.getMinutesPrice())
                            .reservationTime(reservation.getReservationTime())
                            .reservationLocation(reservation.getReserveLocation())
                            .build();

                    return convertToSnapfitUserSummary(reservation.getUserId())
                            .flatMap(user -> {
                                result.setUser(user);
                                return convertToSnapfitUserSummary(reservation.getMakerId());
                            })
                            .flatMap(maker -> {
                                result.setMaker(maker);

                                return postService.findPostById(data.getPostId()).flatMap(this::convertToPostSummary);
                            }).flatMap(postSummaryDto -> {
                                result.setPost(postSummaryDto);

                                return Mono.just(result);
                            });
                });
    }

    private Mono<SnapfitUserSummaryDto> convertToSnapfitUserSummary(long userId) {
        return userService.getSnapfitUser(userId)
                .map(snapfitUser -> SnapfitUserSummaryDto.builder()
                        .id(userId)
                        .nickName(snapfitUser.getNickName())
                        .build());
    }

    private Mono<Reservation> convertToReservation(ReservationRequest reservationRequest) {
        return Mono.just(reservationRequest)
                .map(request -> Reservation.builder()
                        .reserveLocation(reservationRequest.getReservationLocation())
                        .phoneNumber(reservationRequest.getPhoneNumber())
                        .cancelMessage(null)
                        .makerId(reservationRequest.getMakerId())
                        .postId(reservationRequest.getPostId())
                        .minutes(reservationRequest.getMinutes())
                        .email(reservationRequest.getEmail())
                        .build());
    }

    private Mono<Void> assertValidReservation(ReservationRequest reservationRequest) {
        return Mono.just(reservationRequest)
                .flatMap(req -> postService.findPostById(req.getPostId()))
                .filter(post -> {
                    for (PostPrice price : post.getPostPrices()) {
                        if (price.getPrice().equals(reservationRequest.getPrice()) && price.getMinute().equals(reservationRequest.getMinutes())) {
                            return true;
                        }
                    }
                    return false;
                })
                .switchIfEmpty(Mono.error(new ErrorResponse(ReservationErrorCode.INVALID_PRICE)))
                .then(Mono.empty());
    }


    private Mono<PostSummaryDto> convertToPostSummary(Post post) {
        return Mono.just(PostSummaryDto.builder()
                        .id(post.getId())
                        .price(getMinPrice(post.getPostPrices()))
                        .title(post.getTitle())
                        .vibes(post.getPostVibes().stream().map(Vibe::getName).toList())
                        .locations(post.getLocations().stream().map(Location::getAdminName).toList())
                        .thumbNail(post.getThumbnail())
                        .isStudio(post.getIsStudio())
                        .build())
                .flatMap(postSummaryDto -> convertToSnapfitUserSummary(post.getUserId())
                        .map(snapfitUserSummaryDto -> {
                            postSummaryDto.setMaker(snapfitUserSummaryDto);
                            return postSummaryDto;
                        }));
    }

    private int getMinPrice(List<PostPrice> postPrices) {
        int minPrice = Integer.MAX_VALUE;

        for (PostPrice price : postPrices) {
            minPrice = Integer.min(price.getPrice(), minPrice);
        }

        return minPrice;
    }
}
