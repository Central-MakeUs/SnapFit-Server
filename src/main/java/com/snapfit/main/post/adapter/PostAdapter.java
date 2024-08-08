package com.snapfit.main.post.adapter;

import com.snapfit.main.common.annoataion.Adapter;
import com.snapfit.main.common.exception.ErrorResponse;
import com.snapfit.main.post.application.PostService;
import com.snapfit.main.post.application.dto.PostDetailDto;
import com.snapfit.main.post.domain.exception.PostErrorCode;
import com.snapfit.main.post.presentation.dto.CreatePostRequest;
import com.snapfit.main.user.application.UserService;
import com.snapfit.main.user.domain.SnapfitUser;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

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
                .flatMap(postDetailDto -> userService.findNicknameById(userId).map(nickName -> {
                    postDetailDto.setMakerNickName(nickName);
                    return postDetailDto;
                }));
    }

//    public Mono<PostDetailDto> getPostDetail(Long postId, Long userId) {
//        return userService.getSnapfitUser(userId);
//    }

}
