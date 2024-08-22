package com.snapfit.main.post.presentation;

import com.snapfit.main.common.dto.PageResult;
import com.snapfit.main.post.adapter.PostAdapter;
import com.snapfit.main.post.application.dto.LikePostCountDto;
import com.snapfit.main.post.application.dto.PostDetailDto;
import com.snapfit.main.post.application.dto.PostSummaryDto;
import com.snapfit.main.post.domain.exception.PostErrorCode;
import com.snapfit.main.post.presentation.dto.CreatePostRequest;
import com.snapfit.main.reservation.domain.exception.ReservationErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class PostController {
    private final PostAdapter postAdapter;

    @PostMapping("/snapfit/post")
    @Operation(summary = "상품 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "분위기나 장소가 등록되어 있지 않는 경우", content = {@Content(schema = @Schema(implementation = PostErrorCode.class))}),
            @ApiResponse(responseCode = "403", description = "메이커가 아닌 경우", content = {@Content(schema = @Schema(implementation = PostErrorCode.class))})
    })
    public Mono<ResponseEntity<PostDetailDto>> createPost(Authentication authentication, @Valid @RequestBody CreatePostRequest request) {
        return postAdapter.createPost(request, Long.valueOf(authentication.getName())).map(ResponseEntity::ok);
    }


    @GetMapping("/snapfit/post")
    @Operation(summary = "상품 상제 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "해당 post가 존재하지 않는 경우(테스트 필요)", content = {@Content(schema = @Schema(implementation = PostErrorCode.class))}),
    })
    public Mono<ResponseEntity<PostDetailDto>> getPostDetail(Authentication authentication, @RequestParam("id") Long postId) {
        return postAdapter.getPostDetail(postId, Long.valueOf(authentication.getName())).map(ResponseEntity::ok);
    }

    @GetMapping("/snapfit/posts/filter/vibes")
    @Operation(summary = "분위기에 따른 상품 조회", description = "여러 분위기 중 하나라도 포함하고 있는 상품 표출")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "분위기가 등록되어 있지 않는 경우", content = {@Content(schema = @Schema(implementation = PostErrorCode.class))}),
    })
    public Mono<ResponseEntity<PageResult<PostSummaryDto>>> getPosts(Authentication authentication, @RequestParam("vibes") List<String> vibes,
                                                                     @Schema(description = "한 페이지에 들어가는 개수.(1~100)") @Valid @Positive @Max(100) @RequestParam("limit")
                                                                     int limit,
                                                                     @Schema(description = "페이지 수.(0~)") @Valid @PositiveOrZero @RequestParam("offset")
                                                                     int offset) {
        return postAdapter.findByVibe(limit, offset, vibes, Long.parseLong(authentication.getName())).map(ResponseEntity::ok);
    }


    @GetMapping("/snapfit/posts/all")
    @Operation(summary = "모든 상품 조회", description = "필터 없이 상품 조회(전체)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
    })
    public Mono<ResponseEntity<PageResult<PostSummaryDto>>> getAllPosts(Authentication authentication,
                                                                        @Schema(description = "한 페이지에 들어가는 개수.(1~100)") @Valid @Positive @Max(100) @RequestParam("limit")
                                                                        int limit,
                                                                        @Schema(description = "페이지 수.(0~)") @Valid @PositiveOrZero @RequestParam("offset")
                                                                        int offset) {
        return postAdapter.findAll(limit, offset, Long.parseLong(authentication.getName())).map(ResponseEntity::ok);
    }

    @GetMapping("/snafit/posts/maker")
    @Operation(summary = "메이커의 상품 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
    })
    public Mono<ResponseEntity<PageResult<PostSummaryDto>>> getAllPosts(Authentication authentication,
                                                                        @Schema(description = "한 페이지에 들어가는 개수.(1~100)") @Valid @Positive @Max(100) @RequestParam("limit")
                                                                        int limit,
                                                                        @Schema(description = "페이지 수.(0~)") @Valid @PositiveOrZero @RequestParam("offset")
                                                                        int offset,
                                                                        @RequestParam("userId") long userId) {
        return postAdapter.findByMaker(limit, offset, userId, Long.parseLong(authentication.getName())).map(ResponseEntity::ok);
    }

    @PostMapping("/snapfit/post/like")
    @Operation(summary = "상품 찜하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "409", description = "이미 찜한 상품인 경우")
    })
    public Mono<ResponseEntity<Void>> likePost(Authentication authentication, @RequestParam("postId") long postId) {
        return postAdapter.likePost(Long.parseLong(authentication.getName()), postId).map(ResponseEntity::ok);
    }

    @DeleteMapping("/snapfit/post/like")
    @Operation(summary = "상품 찜 취소하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "409", description = "찜하지 않은 상품인 경우")
    })
    public Mono<ResponseEntity<Void>> dislikePost(Authentication authentication, @RequestParam("postId") long postId) {
        return postAdapter.dislikePost(Long.parseLong(authentication.getName()), postId).map(ResponseEntity::ok);
    }

    @GetMapping("/snapfit/post/like/count")
    @Operation(summary = "찜한 상품 개수")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
    })
    public Mono<ResponseEntity<LikePostCountDto>> countLikePosts(Authentication authentication) {
        return postAdapter.countLikePost(Long.parseLong(authentication.getName())).map(ResponseEntity::ok);
    }

    @GetMapping("/snafit/post/like")
    @Operation(summary = "찜한 상품 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
    })
    public Mono<ResponseEntity<PageResult<PostSummaryDto>>> getLikePosts(Authentication authentication,
                                                                         @Schema(description = "한 페이지에 들어가는 개수.(1~100)") @Valid @Positive @Max(100) @RequestParam("limit")
                                                                         int limit,
                                                                         @Schema(description = "페이지 수.(0~)") @Valid @PositiveOrZero @Max(100) @RequestParam("offset")
                                                                         int offset) {
        return postAdapter.getLikePosts(limit, offset, Long.parseLong(authentication.getName())).map(ResponseEntity::ok);
    }
}
