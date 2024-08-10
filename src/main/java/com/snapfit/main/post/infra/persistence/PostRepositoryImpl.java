package com.snapfit.main.post.infra.persistence;

import com.snapfit.main.common.domain.location.Location;
import com.snapfit.main.common.domain.vibe.Vibe;
import com.snapfit.main.common.dto.PageResult;
import com.snapfit.main.common.exception.ErrorResponse;
import com.snapfit.main.post.domain.Post;
import com.snapfit.main.post.domain.PostImage;
import com.snapfit.main.post.domain.PostPrice;
import com.snapfit.main.post.domain.PostRepository;
import com.snapfit.main.post.domain.dto.Price;
import com.snapfit.main.post.domain.exception.PostErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
                .flatMap(insertData -> findById(insertData.getId()));
    }

    @Override
    public Mono<Post> findById(Long id) {
        return databaseClient.sql("""
                        SELECT
                            p.id AS post_id,
                            p.user_id,
                            p.createAt,
                            p.is_studio,
                            p.title,
                            p.description,
                            p.person_price,
                            p.thumbnail,
                            p.is_valid,
                            ARRAY_AGG(DISTINCT v.name) AS vibes,
                            ARRAY_AGG(DISTINCT pi.path) AS post_images,
                            ARRAY_AGG(DISTINCT l.admin_name) AS locations,
                            ARRAY_AGG(DISTINCT pp.price || ':' || pp.minutes) AS prices
                        FROM post p
                        LEFT JOIN post_vibe pv ON p.id = pv.post_id
                        LEFT JOIN vibe_config v ON pv.vibe_id = v.id
                        LEFT JOIN post_image pi ON p.id = pi.post_id
                        LEFT JOIN post_location pl ON p.id = pl.post_id
                        LEFT JOIN location_config l ON pl.location_id = l.id
                        LEFT JOIN post_price pp ON p.id = pp.post_id
                        WHERE p.id = :id
                        GROUP BY p.id
                        """)
                .bind("id", id)
                .map((row, rowMetadata) -> Post.builder()
                        .id(row.get("post_id", Long.class))
                        .userId(row.get("user_id", Long.class))
                        .createAt(row.get("createAt", LocalDateTime.class))
                        .isStudio(row.get("is_studio", Boolean.class))
                        .title(row.get("title", String.class))
                        .desc(row.get("description", String.class))
                        .personPrice(row.get("person_price", Integer.class))
                        .thumbnail(row.get("thumbnail", String.class))
                        .isValid(row.get("is_valid", Boolean.class))
                        .postVibes(Arrays.stream((String[]) row.get("vibes"))
                                .map(name -> new Vibe(null, name))  // `Vibe` 객체로 변환
                                .collect(Collectors.toList()))
                        .postImages(Arrays.asList((String[]) row.get("post_images"))
                                .stream()
                                .map(path -> PostImage.builder().path(path).build())  // `PostImage` 객체로 변환
                                .collect(Collectors.toList()))
                        .locations(Arrays.asList((String[]) row.get("locations"))
                                .stream()
                                .map(adminName -> Location.builder().adminName(adminName).build())  // `Location` 객체로 변환
                                .collect(Collectors.toList()))
                        .postPrices(Arrays.stream((String[]) row.get("prices"))
                                .map(priceStr -> {
                                    String[] parts = priceStr.split(":");
                                    return PostPrice.builder()
                                            .minute(Integer.parseInt(parts[1]))
                                            .price(Integer.parseInt(parts[0]))
                                            .build();
                                })
                                .collect(Collectors.toList()))
                        .build()
                )
                .one()
                .switchIfEmpty(Mono.error(new ErrorResponse(PostErrorCode.NOT_EXIST_POST)));
    }

    @Override
    public Mono<PageResult<Post>> findByVibes(int limit, int offset, Vibe vibes) {
        return databaseClient.sql("""
                        SELECT
                            p.id AS post_id,
                            p.user_id,
                            p.createAt,
                            p.is_studio,
                            p.title,
                            p.description,
                            p.person_price,
                            p.thumbnail,
                            p.is_valid,
                            ARRAY_AGG(DISTINCT v.name) AS vibes,
                            ARRAY_AGG(DISTINCT pi.path) AS post_images,
                            ARRAY_AGG(DISTINCT l.admin_name) AS locations,
                            ARRAY_AGG(DISTINCT pp.price || ':' || pp.minutes) AS prices
                        FROM post p
                        LEFT JOIN post_vibe pv ON p.id = pv.post_id
                        LEFT JOIN vibe_config v ON pv.vibe_id = v.id
                        LEFT JOIN post_image pi ON p.id = pi.post_id
                        LEFT JOIN post_location pl ON p.id = pl.post_id
                        LEFT JOIN location_config l ON pl.location_id = l.id
                        LEFT JOIN post_price pp ON p.id = pp.post_id
                        WHERE is_valid = true
                        GROUP BY p.id
                        HAVING :vibeName = ANY(ARRAY_AGG(v.name))
                        ORDER BY p.id
                        LIMIT :limit OFFSET :offset
                        """)
                .bind("vibeName", vibes.getName())
                .bind("limit", limit)
                .bind("offset", offset)
                .map((row, rowMetadata) -> Post.builder()
                        .id(row.get("post_id", Long.class))
                        .userId(row.get("user_id", Long.class))
                        .createAt(row.get("createAt", LocalDateTime.class))
                        .isStudio(row.get("is_studio", Boolean.class))
                        .title(row.get("title", String.class))
                        .desc(row.get("description", String.class))
                        .personPrice(row.get("person_price", Integer.class))
                        .thumbnail(row.get("thumbnail", String.class))
                        .isValid(row.get("is_valid", Boolean.class))
                        .postVibes(Arrays.stream((String[]) row.get("vibes"))
                                .map(name -> new Vibe(null, name))  // `Vibe` 객체로 변환
                                .collect(Collectors.toList()))
                        .postImages(Arrays.asList((String[]) row.get("post_images"))
                                .stream()
                                .map(path -> PostImage.builder().path(path).build())  // `PostImage` 객체로 변환
                                .collect(Collectors.toList()))
                        .locations(Arrays.asList((String[]) row.get("locations"))
                                .stream()
                                .map(adminName -> Location.builder().adminName(adminName).build())  // `Location` 객체로 변환
                                .collect(Collectors.toList()))
                        .postPrices(Arrays.stream((String[]) row.get("prices"))
                                .map(priceStr -> {
                                    String[] parts = priceStr.split(":");
                                    return PostPrice.builder()
                                            .minute(Integer.parseInt(parts[1]))
                                            .price(Integer.parseInt(parts[0]))
                                            .build();
                                })
                                .collect(Collectors.toList()))
                        .build()
                )
                .all()
                .collectList()
                .switchIfEmpty(Mono.just(new ArrayList<Post>()))
                .map(posts -> PageResult.<Post>builder()
                        .offset(offset)
                        .limit(limit)
                        .data(posts)
                        .build());
    }

    @Override
    public Mono<PageResult<Post>> findAll(int limit, int offset) {
        return databaseClient.sql("""
                        SELECT
                            p.id AS post_id,
                            p.user_id,
                            p.createAt,
                            p.is_studio,
                            p.title,
                            p.description,
                            p.person_price,
                            p.thumbnail,
                            p.is_valid,
                            ARRAY_AGG(DISTINCT v.name) AS vibes,
                            ARRAY_AGG(DISTINCT pi.path) AS post_images,
                            ARRAY_AGG(DISTINCT l.admin_name) AS locations,
                            ARRAY_AGG(DISTINCT pp.price || ':' || pp.minutes) AS prices
                        FROM post p
                        LEFT JOIN post_vibe pv ON p.id = pv.post_id
                        LEFT JOIN vibe_config v ON pv.vibe_id = v.id
                        LEFT JOIN post_image pi ON p.id = pi.post_id
                        LEFT JOIN post_location pl ON p.id = pl.post_id
                        LEFT JOIN location_config l ON pl.location_id = l.id
                        LEFT JOIN post_price pp ON p.id = pp.post_id
                        WHERE is_valid = true
                        GROUP BY p.id
                        ORDER BY p.id
                        LIMIT :limit OFFSET :offset
                        """)
                .bind("limit", limit)
                .bind("offset", offset)
                .map((row, rowMetadata) -> Post.builder()
                        .id(row.get("post_id", Long.class))
                        .userId(row.get("user_id", Long.class))
                        .createAt(row.get("createAt", LocalDateTime.class))
                        .isStudio(row.get("is_studio", Boolean.class))
                        .title(row.get("title", String.class))
                        .desc(row.get("description", String.class))
                        .personPrice(row.get("person_price", Integer.class))
                        .thumbnail(row.get("thumbnail", String.class))
                        .isValid(row.get("is_valid", Boolean.class))
                        .postVibes(Arrays.stream((String[]) row.get("vibes"))
                                .map(name -> new Vibe(null, name))  // `Vibe` 객체로 변환
                                .collect(Collectors.toList()))
                        .postImages(Arrays.stream((String[]) row.get("post_images"))
                                .map(path -> PostImage.builder().path(path).build())  // `PostImage` 객체로 변환
                                .collect(Collectors.toList()))
                        .locations(Arrays.asList((String[]) row.get("locations"))
                                .stream()
                                .map(adminName -> Location.builder().adminName(adminName).build())  // `Location` 객체로 변환
                                .collect(Collectors.toList()))
                        .postPrices(Arrays.stream((String[]) row.get("prices"))
                                .map(priceStr -> {
                                    String[] parts = priceStr.split(":");
                                    return PostPrice.builder()
                                            .minute(Integer.parseInt(parts[1]))
                                            .price(Integer.parseInt(parts[0]))
                                            .build();
                                })
                                .collect(Collectors.toList()))
                        .build()
                )
                .all()
                .collectList()
                .switchIfEmpty(Mono.just(new ArrayList<Post>()))
                .map(posts -> PageResult.<Post>builder()
                        .offset(offset)
                        .limit(limit)
                        .data(posts)
                        .build());
    }


    private Mono<Post> savePost(Post post) {
        return databaseClient.sql("INSERT INTO post (user_id, is_studio, title, description, person_price, thumbnail, is_valid) " +
                        "VALUES (:userId, :isStudio, :title, :desc, :personPrice, :thumbnail, :isValid) RETURNING id, createAt")
                .bind("userId", post.getUserId())
                .bind("isStudio", post.getIsStudio())
                .bind("title", post.getTitle())
                .bind("desc", post.getDesc())
                .bind("personPrice", post.getPersonPrice())
                .bind("thumbnail", post.getThumbnail())
                .bind("isValid", post.getIsValid())
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

//    private Mono<Post> populateTransientFields(Post post) {
//        return Mono.zip(
//                getPostImages(post.getId()).collectList(),
//                getPostPrices(post.getId()).collectList(),
//                getPostVibes(post.getId()).collectList(),
//                getLocations(post.getId()).collectList()
//        ).map(tuple -> {
//            post.setPostImages(tuple.getT1());
//            post.setPostPrices(tuple.getT2());
//            post.setPostVibes(tuple.getT3());
//            post.setLocations(tuple.getT4());
//            return post;
//        });
//    }
//
//    private Flux<PostImage> getPostImages(Long postId) {
//        return databaseClient.sql("SELECT * FROM post_image WHERE post_id = :postId")
//                .bind("postId", postId)
//                .map((row, rowMetadata) -> new PostImage(
//                        row.get("id", Long.class),
//                        row.get("post_id", Long.class),
//                        row.get("path", String.class)
//                )).all();
//    }
//
//    private Flux<PostPrice> getPostPrices(Long postId) {
//        return databaseClient.sql("SELECT * FROM post_price WHERE post_id = :postId")
//                .bind("postId", postId)
//                .map((row, rowMetadata) -> new PostPrice(
//                        row.get("id", Long.class),
//                        row.get("post_id", Long.class),
//                        row.get("minutes", Integer.class),
//                        row.get("price", Integer.class)
//                )).all();
//    }
//
//    private Flux<Vibe> getPostVibes(Long postId) {
//        return databaseClient.sql("SELECT v.* FROM post_vibe pv JOIN vibe_config v ON pv.vibe_id = v.id WHERE pv.post_id = :postId")
//                .bind("postId", postId)
//                .map((row, rowMetadata) -> new Vibe(
//                        row.get("id", Long.class),
//                        row.get("name", String.class)
//                )).all();
//    }
//
//    private Flux<Location> getLocations(Long postId) {
//        return databaseClient.sql("SELECT l.* FROM post_location pl JOIN location_config l ON pl.location_id = l.id WHERE pl.post_id = :postId")
//                .bind("postId", postId)
//                .map((row, rowMetadata) -> new Location(
//                        row.get("id", Long.class),
//                        row.get("admin_name", String.class)
//                )).all();
//    }
}
