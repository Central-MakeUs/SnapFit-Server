package com.snapfit.main.post.domain;

import com.snapfit.main.common.domain.location.Location;
import com.snapfit.main.common.domain.vibe.Vibe;
import com.snapfit.main.common.dto.PageResult;
import com.snapfit.main.post.domain.dto.Price;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PostRepository {
    Mono<Post> save(Post post, List<String> imagePaths, List<Location> locations, List<Vibe> vibes, List<Price> prices, long userId);
    Mono<Post> findById(Long id, long userId);
    Mono<PageResult<Post>> findByVibes(int limit, int offset, List<Vibe> vibes, long userId);
    Mono<PageResult<Post>> findAll(int limit, int offset, long userId);

    Mono<PageResult<Post>> findByMaker(int limit, int offset, long makerId, long userId);
    Mono<Void> likePost(long userId, long postId);
    Mono<Void> disLikePost(long userId, long postId);
    Mono<Integer> countLikePost(long userId);
    Mono<PageResult<Post>> findLikePost(int limit, int offset, long userId);
}
