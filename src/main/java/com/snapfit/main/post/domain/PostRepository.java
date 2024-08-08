package com.snapfit.main.post.domain;

import com.snapfit.main.common.domain.location.Location;
import com.snapfit.main.common.domain.vibe.Vibe;
import com.snapfit.main.post.domain.dto.Price;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PostRepository {
    Mono<Post> save(Post post, List<String> imagePaths, List<Location> locations, List<Vibe> vibes, List<Price> prices);
    //TODO r2dbc page 조사 필요

}
