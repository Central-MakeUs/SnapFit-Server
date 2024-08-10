package com.snapfit.main.post.presentation;

import com.snapfit.main.common.dto.PageResult;
import com.snapfit.main.post.adapter.PostAdapter;
import com.snapfit.main.post.application.dto.PostDetailDto;
import com.snapfit.main.post.application.dto.PostSummaryDto;
import com.snapfit.main.post.presentation.dto.CreatePostRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
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
    public Mono<ResponseEntity<PostDetailDto>> createPost(Authentication authentication, @Valid @RequestBody CreatePostRequest request) {
        return postAdapter.createPost(request, Long.valueOf(authentication.getName())).map(ResponseEntity::ok);
    }

    @GetMapping("/snapfit/post")
    public Mono<ResponseEntity<PostDetailDto>> getPostDetail(Authentication authentication, @RequestParam("id") Long postId) {
        return postAdapter.getPostDetail(postId, Long.valueOf(authentication.getName())).map(ResponseEntity::ok);
    }

    @GetMapping("/snapfit/posts/filter/vibes")
    public Mono<ResponseEntity<PageResult<PostSummaryDto>>> getPosts(Authentication authentication, @RequestParam("vibes") List<String> vibes, @RequestParam("limit") int limit, @RequestParam("offset") int offset) {
        return postAdapter.findByVibe(limit, offset, vibes).map(ResponseEntity::ok);
    }


    @GetMapping("/snapfit/posts/all")
    public Mono<ResponseEntity<PageResult<PostSummaryDto>>> getAllPosts(Authentication authentication, @RequestParam("limit") int limit, @RequestParam("offset") int offset) {
        return postAdapter.findAll(limit, offset).map(ResponseEntity::ok);
    }
}
