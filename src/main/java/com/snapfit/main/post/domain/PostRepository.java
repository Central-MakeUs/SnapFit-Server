package com.snapfit.main.post.domain;

import com.snapfit.main.common.domain.location.Location;
import com.snapfit.main.common.domain.vibe.Vibe;
import com.snapfit.main.common.dto.PageResult;
import com.snapfit.main.post.domain.dto.Price;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PostRepository {
    Mono<Post> save(Post post, List<String> imagePaths, List<Location> locations, List<Vibe> vibes, List<Price> prices);

    Mono<Post> findById(Long id);

    //TODO 나중에 여러 개로도 필터링 걸 수 있는 방법 필요.
    Mono<PageResult<Post>> findByVibes(int limit, int offset, Vibe vibes);
    Mono<PageResult<Post>> findAll(int limit, int offset);

    //TODO 유저가 작성한 글 목록 볼 수 있는 페이지 필요.
}
