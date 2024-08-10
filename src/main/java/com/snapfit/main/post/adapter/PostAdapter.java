package com.snapfit.main.post.adapter;

import com.snapfit.main.common.annoataion.Adapter;
import com.snapfit.main.common.domain.location.Location;
import com.snapfit.main.common.domain.vibe.Vibe;
import com.snapfit.main.common.domain.vibe.VibeFinder;
import com.snapfit.main.common.dto.PageResult;
import com.snapfit.main.common.exception.ErrorResponse;
import com.snapfit.main.post.application.PostService;
import com.snapfit.main.post.application.dto.PostDetailDto;
import com.snapfit.main.post.application.dto.PostSummaryDto;
import com.snapfit.main.post.application.dto.SnapfitUserSummaryDto;
import com.snapfit.main.post.domain.Post;
import com.snapfit.main.post.domain.PostPrice;
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
    private final VibeFinder vibeFinder;

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

    public Mono<PageResult<PostSummaryDto>> findByVibe(int limit, int offset, List<String> vibes) {
        return postService.findByVibe(limit, offset, vibes)
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

    public Mono<PageResult<PostSummaryDto>> findAll(int limit, int offset) {
        return postService.find(limit, offset)
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


        //TODO 좋아요 조회 기능 구현 필요....
    public Mono<PostDetailDto> getPostDetail(Long postId, Long userId) {
        return postService.findPostDetailById(postId);
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
