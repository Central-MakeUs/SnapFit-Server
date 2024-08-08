package com.snapfit.main.post.infra.persistence;

import com.snapfit.main.common.domain.location.Location;
import com.snapfit.main.common.domain.vibe.Vibe;
import com.snapfit.main.post.domain.Post;
import com.snapfit.main.post.domain.PostImage;
import com.snapfit.main.post.domain.PostPrice;
import com.snapfit.main.post.domain.PostRepository;
import com.snapfit.main.post.domain.dto.Price;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

    private final DatabaseClient databaseClient;

    @Override
    @Transactional
    public Mono<Post> save(Post post, List<String> imagePaths, List<Location> locations, List<Vibe> vibes, List<Price> prices) {
        return savePost(post)
                .flatMap(savedPost -> saveRelatedEntities(savedPost.getId(), imagePaths, locations, vibes, prices)
                        .then(Mono.just(savedPost)))
                .flatMap(this::populateTransientFields);
    }


    private Mono<Post> savePost(Post post) {
        return databaseClient.sql("INSERT INTO post (user_id, is_studio, title, description, person_price, thumbnail) " +
                        "VALUES (:userId, :isStudio, :title, :desc, :personPrice, :thumbnail) RETURNING id, createAt")
                .bind("userId", post.getUserId())
                .bind("isStudio", post.getIsStudio())
                .bind("title", post.getTitle())
                .bind("desc", post.getDesc())
                .bind("personPrice", post.getPersonPrice())
                .bind("thumbnail", post.getThumbnail())
                .map(row -> {
                    post.setId(row.get("id", Long.class));
                    post.setCreateAt(row.get("createAt", LocalDateTime.class));

                    return row;
                }).one()
                .then(Mono.just(post));
    }

    private Mono<Void> saveRelatedEntities(Long postId, List<String> imagePaths, List<Location> locations, List<Vibe> vibes, List<Price> prices) {
        return Flux.concat(
                savePostImages(postId, imagePaths),
                savePostLocations(postId, locations),
                savePostVibes(postId, vibes),
                savePostPrice(postId, prices)
        ).then();
    }

    private Flux<Void> savePostImages(Long postId, List<String> imagePaths) {
        return Flux.fromIterable(imagePaths)
                .flatMap(path -> databaseClient.sql("INSERT INTO post_image (post_id, path) VALUES (:postId, :path)")
                        .bind("postId", postId)
                        .bind("path", path)
                        .fetch()
                        .rowsUpdated()
                        .then());
    }

    private Flux<Void> savePostLocations(Long postId, List<Location> locations) {
        return Flux.fromIterable(locations)
                .flatMap(location -> databaseClient.sql("INSERT INTO post_location (post_id, location_id) VALUES (:postId, :locationId)")
                        .bind("postId", postId)
                        .bind("locationId", location.getId())
                        .fetch()
                        .rowsUpdated()
                        .then());
    }

    private Flux<Void> savePostVibes(Long postId, List<Vibe> vibes) {
        return Flux.fromIterable(vibes)
                .flatMap(vibe -> databaseClient.sql("INSERT INTO post_vibe (post_id, vibe_id) VALUES (:postId, :vibeId)")
                        .bind("postId", postId)
                        .bind("vibeId", vibe.getId())
                        .fetch()
                        .rowsUpdated()
                        .then());
    }

    private Flux<Void> savePostPrice(Long postId, List<Price> prices) {
        return Flux.fromIterable(prices)
                .flatMap(price -> databaseClient.sql("INSERT INTO post_price (post_id, minutes, price) VALUES (:postId, :min, :price)")
                        .bind("postId", postId)
                        .bind("min", price.getMin())
                        .bind("price", price.getPrice())
                        .fetch()
                        .rowsUpdated()
                        .then());
    }

    private Mono<Post> populateTransientFields(Post post) {
        return Mono.zip(
                getPostImages(post.getId()).collectList(),
                getPostPrices(post.getId()).collectList(),
                getPostVibes(post.getId()).collectList(),
                getLocations(post.getId()).collectList()
        ).map(tuple -> {
            post.setPostImages(tuple.getT1());
            post.setPostPrices(tuple.getT2());
            post.setPostVibes(tuple.getT3());
            post.setLocations(tuple.getT4());
            return post;
        });
    }

    private Flux<PostImage> getPostImages(Long postId) {
        return databaseClient.sql("SELECT * FROM post_image WHERE post_id = :postId")
                .bind("postId", postId)
                .map((row, rowMetadata) -> new PostImage(
                        row.get("id", Long.class),
                        row.get("post_id", Long.class),
                        row.get("path", String.class)
                )).all();
    }

    private Flux<PostPrice> getPostPrices(Long postId) {
        return databaseClient.sql("SELECT * FROM post_price WHERE post_id = :postId")
                .bind("postId", postId)
                .map((row, rowMetadata) -> new PostPrice(
                        row.get("id", Long.class),
                        row.get("post_id", Long.class),
                        row.get("minutes", Integer.class),
                        row.get("price", Integer.class)
                )).all();
    }

    private Flux<Vibe> getPostVibes(Long postId) {
        return databaseClient.sql("SELECT v.* FROM post_vibe pv JOIN vibe_config v ON pv.vibe_id = v.id WHERE pv.post_id = :postId")
                .bind("postId", postId)
                .map((row, rowMetadata) -> new Vibe(
                        row.get("id", Long.class),
                        row.get("name", String.class)
                )).all();
    }

    private Flux<Location> getLocations(Long postId) {
        return databaseClient.sql("SELECT l.* FROM post_location pl JOIN location_config l ON pl.location_id = l.id WHERE pl.post_id = :postId")
                .bind("postId", postId)
                .map((row, rowMetadata) -> new Location(
                        row.get("id", Long.class),
                        row.get("admin_name", String.class)
                )).all();
    }
}
