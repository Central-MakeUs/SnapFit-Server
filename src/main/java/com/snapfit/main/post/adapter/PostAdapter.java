package com.snapfit.main.post.adapter;

import com.snapfit.main.common.annoataion.Adapter;
import com.snapfit.main.common.domain.location.Location;
import com.snapfit.main.common.domain.vibe.Vibe;
import com.snapfit.main.common.domain.vibe.VibeFinder;
import com.snapfit.main.common.dto.PageResult;
import com.snapfit.main.common.exception.ErrorResponse;
import com.snapfit.main.post.application.PostService;
import com.snapfit.main.post.application.dto.LikePostCountDto;
import com.snapfit.main.post.application.dto.PostDetailDto;
import com.snapfit.main.post.application.dto.PostSummaryDto;
import com.snapfit.main.post.application.dto.SnapfitUserSummaryDto;
import com.snapfit.main.post.domain.Post;
import com.snapfit.main.post.domain.PostImage;
import com.snapfit.main.post.domain.PostPrice;
import com.snapfit.main.post.domain.dto.Price;
import com.snapfit.main.post.domain.exception.PostErrorCode;
import com.snapfit.main.post.presentation.dto.CreatePostRequest;
import com.snapfit.main.user.application.UserService;
import com.snapfit.main.user.domain.SnapfitUser;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Adapter
@RequiredArgsConstructor
public class PostAdapter {
    private final UserService userService;
    private final PostService postService;

    public Mono<PostDetailDto> createPost(CreatePostRequest request, Long userId) {
        return userService.getSnapfitUser(userId)
                .filter(SnapfitUser::isPhotographer)
                .switchIfEmpty(Mono.error(new ErrorResponse(PostErrorCode.NOT_MAKER)))
                .flatMap(snapfitUser -> postService.createPost(userId, request))
                //TODO service 단 내용 불러와야 함.
                .flatMap(postDetailDto -> convertToSnapfitUserSummary(userId).map(userSummary -> {
                    postDetailDto.setMaker(userSummary);
                    return postDetailDto;
                }));
    }

    public Mono<PageResult<PostSummaryDto>> findByVibe(int limit, int offset, List<String> vibes, long userId) {
        return postService.findByVibe(limit, offset, vibes, userId)
                .flatMap(postPageResult ->
                        Flux.fromIterable(postPageResult.getData())
                                .flatMap(this::convertToPostSummary)
                                .collectList()
                                .map(postSummaryDtos -> PageResult.<PostSummaryDto>builder()
                                        .limit(postPageResult.getLimit())
                                        .offset(postPageResult.getOffset())
                                        .data(postSummaryDtos)
                                        .build())
                );
    }

    public Mono<PageResult<PostSummaryDto>> findAll(int limit, int offset, long userId) {
        return postService.find(limit, offset, userId)
                .flatMap(postPageResult ->
                        Flux.fromIterable(postPageResult.getData())
                                .flatMap(this::convertToPostSummary)
                                .collectList()
                                .map(postSummaryDtos -> PageResult.<PostSummaryDto>builder()
                                        .limit(postPageResult.getLimit())
                                        .offset(postPageResult.getOffset())
                                        .data(postSummaryDtos)
                                        .build())
                );
    }

    public Mono<PageResult<PostSummaryDto>> findByMaker(int limit, int offset, long makerId, long userId) {
        return postService.findByMaker(limit, offset, makerId, userId).flatMap(postPageResult ->
                Flux.fromIterable(postPageResult.getData())
                        .flatMap(this::convertToPostSummary)
                        .collectList()
                        .map(postSummaryDtos -> PageResult.<PostSummaryDto>builder()
                                .limit(postPageResult.getLimit())
                                .offset(postPageResult.getOffset())
                                .data(postSummaryDtos)
                                .build())
        );
    }


    public Mono<PostDetailDto> getPostDetail(long postId, long userId) {
        return postService.findPostById(postId, userId)
                .flatMap(this::convertToPostDetailDto);

    }

    public Mono<Void> likePost(long userId, long postId) {
        return postService.likePost(userId, postId);
    }

    public Mono<Void> dislikePost(long userId, long postId) {
        return postService.dislikePost(userId, postId);
    }

    public Mono<LikePostCountDto> countLikePost(long userId) {
        return postService.countLikePost(userId)
                .map(data -> LikePostCountDto.builder().count(data).build());
    }

    public Mono<PageResult<PostSummaryDto>> getLikePosts(int limit, int offset, long userId) {
        return postService.getLikePosts(limit, offset, userId)
                .flatMap(postPageResult ->
                        Flux.fromIterable(postPageResult.getData())
                                .flatMap(this::convertToPostSummary)
                                .collectList()
                                .map(postSummaryDtos -> PageResult.<PostSummaryDto>builder()
                                        .limit(postPageResult.getLimit())
                                        .offset(postPageResult.getOffset())
                                        .data(postSummaryDtos)
                                        .build())
                );
    }

    private Mono<PostDetailDto> convertToPostDetailDto(Post post) {
        return Mono.just(PostDetailDto.builder()
                        .id(post.getId())
                        .createAt(post.getCreateAt())
                        .thumbnail(post.getThumbnail())
                        .images(post.getPostImages().stream().map(PostImage::getPath).toList())
                        .desc(post.getDesc())
                        .title(post.getTitle())
                        .personPrice(post.getPersonPrice())
                        .isLike(post.getIsLike())
                        .vibes(post.getPostVibes().stream().map(Vibe::getName).toList())
                        .locations(post.getLocations().stream().map(Location::getAdminName).toList())
                        .isStudio(post.getIsStudio())
                        .prices(post.getPostPrices().stream().map(postPrice -> new Price(postPrice.getMinute(), postPrice.getPrice())).toList())
                        .build())
                .flatMap(detailDto -> {
                    return convertToSnapfitUserSummary(post.getUserId())
                            .map(snapfitUserSummaryDto -> {
                                detailDto.setMaker(snapfitUserSummaryDto);

                                return detailDto;
                            });
                });
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
                        .isLike(post.getIsLike())
                        .build())
                .flatMap(postSummaryDto -> convertToSnapfitUserSummary(post.getUserId())
                        .map(snapfitUserSummaryDto -> {
                            postSummaryDto.setMaker(snapfitUserSummaryDto);
                            return postSummaryDto;
                        }));
    }

    private Mono<SnapfitUserSummaryDto> convertToSnapfitUserSummary(long userId) {
        return userService.getSnapfitUser(userId)
                .map(snapfitUser -> SnapfitUserSummaryDto.builder()
                        .id(userId)
                        .nickName(snapfitUser.getNickName())
                        .build());
    }

    private int getMinPrice(List<PostPrice> postPrices) {
        int minPrice = Integer.MAX_VALUE;

        for (PostPrice price : postPrices) {
            minPrice = Integer.min(price.getPrice(), minPrice);
        }

        return minPrice;
    }

}
