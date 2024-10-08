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
    public Mono<Post> save(Post post, List<String> imagePaths, List<Location> locations, List<Vibe> vibes, List<Price> prices, long userId) {
        return savePost(post)
                .flatMap(savedPost -> saveRelatedEntities(savedPost.getId(), imagePaths, locations, vibes, prices)
                        .then(Mono.just(savedPost)))
                .flatMap(insertData -> findById(insertData.getId(), userId));
    }

    @Override
    public Mono<Post> findById(Long id, long userId) {
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
                            ARRAY_AGG(DISTINCT pp.price || ':' || pp.minutes) AS prices,
                            CASE WHEN lp.id IS NOT NULL THEN true ELSE false END AS is_like
                        FROM post p
                        LEFT JOIN post_vibe pv ON p.id = pv.post_id
                        LEFT JOIN vibe_config v ON pv.vibe_id = v.id
                        LEFT JOIN post_image pi ON p.id = pi.post_id
                        LEFT JOIN post_location pl ON p.id = pl.post_id
                        LEFT JOIN location_config l ON pl.location_id = l.id
                        LEFT JOIN post_price pp ON p.id = pp.post_id
                        LEFT JOIN like_post lp ON p.id = lp.post_id AND lp.user_id = :userId
                        WHERE p.id = :id
                        GROUP BY p.id, lp.id
                        """)
                .bind("id", id)
                .bind("userId", userId)
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
                        .isLike(row.get("is_like", Boolean.class))
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
    public Mono<PageResult<Post>> findByVibes(int limit, int offset, List<Vibe> vibes, long userId) {
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
                            ARRAY_AGG(DISTINCT pp.price || ':' || pp.minutes) AS prices,
                            CASE WHEN lp.id IS NOT NULL THEN true ELSE false END AS is_like
                        FROM post p
                        LEFT JOIN post_vibe pv ON p.id = pv.post_id
                        LEFT JOIN vibe_config v ON pv.vibe_id = v.id
                        LEFT JOIN post_image pi ON p.id = pi.post_id
                        LEFT JOIN post_location pl ON p.id = pl.post_id
                        LEFT JOIN location_config l ON pl.location_id = l.id
                        LEFT JOIN post_price pp ON p.id = pp.post_id
                        LEFT JOIN like_post lp ON p.id = lp.post_id AND lp.user_id = :userId
                        WHERE is_valid = true
                        GROUP BY p.id, lp.id
                        HAVING ARRAY_AGG(v.name) && :vibeNames::varchar[]
                        ORDER BY p.id
                        LIMIT :limit OFFSET :offset
                        """)
                .bind("vibeNames", vibes.stream().map(Vibe::getName).toList().toArray())
                .bind("limit", limit)
                .bind("offset", offset)
                .bind("userId", userId)
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
                        .isLike(row.get("is_like", Boolean.class))
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
    public Mono<PageResult<Post>> findAll(int limit, int offset, long userId) {
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
                            ARRAY_AGG(DISTINCT pp.price || ':' || pp.minutes) AS prices,
                            CASE WHEN lp.id IS NOT NULL THEN true ELSE false END AS is_like
                        FROM post p
                        LEFT JOIN post_vibe pv ON p.id = pv.post_id
                        LEFT JOIN vibe_config v ON pv.vibe_id = v.id
                        LEFT JOIN post_image pi ON p.id = pi.post_id
                        LEFT JOIN post_location pl ON p.id = pl.post_id
                        LEFT JOIN location_config l ON pl.location_id = l.id
                        LEFT JOIN post_price pp ON p.id = pp.post_id
                        LEFT JOIN like_post lp ON p.id = lp.post_id AND lp.user_id = :userId
                        WHERE is_valid = true
                        GROUP BY p.id, lp.id
                        ORDER BY p.id
                        LIMIT :limit OFFSET :offset
                        """)
                .bind("limit", limit)
                .bind("offset", offset)
                .bind("userId", userId)
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
                        .isLike(row.get("is_like", Boolean.class))
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

    @Override
    public Mono<PageResult<Post>> findByMaker(int limit, int offset, long makerId, long userId) {
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
                            ARRAY_AGG(DISTINCT pp.price || ':' || pp.minutes) AS prices,
                            CASE WHEN lp.id IS NOT NULL THEN true ELSE false END AS is_like
                        FROM post p
                        LEFT JOIN post_vibe pv ON p.id = pv.post_id
                        LEFT JOIN vibe_config v ON pv.vibe_id = v.id
                        LEFT JOIN post_image pi ON p.id = pi.post_id
                        LEFT JOIN post_location pl ON p.id = pl.post_id
                        LEFT JOIN location_config l ON pl.location_id = l.id
                        LEFT JOIN post_price pp ON p.id = pp.post_id
                        LEFT JOIN like_post lp ON p.id = lp.post_id AND lp.user_id = :userId
                        WHERE is_valid = true and p.user_id = :makerId
                        GROUP BY p.id, lp.id
                        ORDER BY p.id
                        LIMIT :limit OFFSET :offset
                        """)
                .bind("limit", limit)
                .bind("offset", offset)
                .bind("makerId", makerId)
                .bind("userId", userId)
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
                        .isLike(row.get("is_like", Boolean.class))
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

    @Override
    public Mono<Void> likePost(long userId, long postId) {
        return alreadyLike(postId, userId)
                .filter(exist -> !exist)
                .switchIfEmpty(Mono.error(new ErrorResponse(PostErrorCode.ALREADY_LIKE_POST)))
                .flatMap(notExist -> databaseClient.sql("""
                                INSERT INTO like_post (user_id, post_id)
                                VALUES (:userId, :postId)
                                ON CONFLICT DO NOTHING
                                """)
                        .bind("userId", userId)
                        .bind("postId", postId)
                        .then())
                ;
    }

    @Override
    public Mono<Void> disLikePost(long userId, long postId) {
        return alreadyLike(postId, userId)
                .filter(exist -> exist)
                .switchIfEmpty(Mono.error(new ErrorResponse(PostErrorCode.ALREADY_LIKE_POST)))
                .flatMap(exist ->databaseClient.sql("""
                        DELETE FROM like_post
                        WHERE user_id = :userId AND post_id = :postId
                        """)
                .bind("userId", userId)
                .bind("postId", postId)
                .then());
    }

    @Override
    public Mono<Integer> countLikePost(long userId) {
        return databaseClient.sql("""
                        SELECT count(*)
                        FROM like_post
                        WHERE user_id = :userId
                        """)
                .bind("userId", userId)
                .map((row, rowMetadata) -> row.get(0, Integer.class))
                .one();
    }

    @Override
    public Mono<PageResult<Post>> findLikePost(int limit, int offset, long userId) {
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
                            ARRAY_AGG(DISTINCT pp.price || ':' || pp.minutes) AS prices,
                            CASE WHEN lp.id IS NOT NULL THEN true ELSE false END AS is_like
                        FROM post p
                        LEFT JOIN post_vibe pv ON p.id = pv.post_id
                        LEFT JOIN vibe_config v ON pv.vibe_id = v.id
                        LEFT JOIN post_image pi ON p.id = pi.post_id
                        LEFT JOIN post_location pl ON p.id = pl.post_id
                        LEFT JOIN location_config l ON pl.location_id = l.id
                        LEFT JOIN post_price pp ON p.id = pp.post_id
                        LEFT JOIN like_post lp ON p.id = lp.post_id AND lp.user_id = :userId
                        WHERE is_valid = true and lp.id is not null
                        GROUP BY p.id, lp.id
                        ORDER BY p.id
                        LIMIT :limit OFFSET :offset
                        """)
                .bind("limit", limit)
                .bind("offset", offset)
                .bind("userId", userId)
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
                        .isLike(row.get("is_like", Boolean.class))
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

    @Override
    public Mono<Void> leaveMaker(long makerId) {
        return databaseClient.sql("""
                UPDATE post SET is_valid = false
                WHERE user_id = :makerId
                """)
                .bind("makerId", makerId)
                .then();
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

    private Mono<Void> saveRelatedEntities(long postId, List<String> imagePaths, List<Location> locations, List<Vibe> vibes, List<Price> prices) {
        return Flux.concat(
                savePostImages(postId, imagePaths),
                savePostLocations(postId, locations),
                savePostVibes(postId, vibes),
                savePostPrice(postId, prices)
        ).then();
    }

    private Flux<Void> savePostImages(long postId, List<String> imagePaths) {
        return Flux.fromIterable(imagePaths)
                .flatMap(path -> databaseClient.sql("INSERT INTO post_image (post_id, path) VALUES (:postId, :path)")
                        .bind("postId", postId)
                        .bind("path", path)
                        .fetch()
                        .rowsUpdated()
                        .then());
    }

    private Flux<Void> savePostLocations(long postId, List<Location> locations) {
        return Flux.fromIterable(locations)
                .flatMap(location -> databaseClient.sql("INSERT INTO post_location (post_id, location_id) VALUES (:postId, :locationId)")
                        .bind("postId", postId)
                        .bind("locationId", location.getId())
                        .fetch()
                        .rowsUpdated()
                        .then());
    }

    private Flux<Void> savePostVibes(long postId, List<Vibe> vibes) {
        return Flux.fromIterable(vibes)
                .flatMap(vibe -> databaseClient.sql("INSERT INTO post_vibe (post_id, vibe_id) VALUES (:postId, :vibeId)")
                        .bind("postId", postId)
                        .bind("vibeId", vibe.getId())
                        .fetch()
                        .rowsUpdated()
                        .then());
    }

    private Flux<Void> savePostPrice(long postId, List<Price> prices) {
        return Flux.fromIterable(prices)
                .flatMap(price -> databaseClient.sql("INSERT INTO post_price (post_id, minutes, price) VALUES (:postId, :min, :price)")
                        .bind("postId", postId)
                        .bind("min", price.getMin())
                        .bind("price", price.getPrice())
                        .fetch()
                        .rowsUpdated()
                        .then());
    }

    private Mono<Boolean> alreadyLike(long postId, long userId) {
        return databaseClient.sql("""
                        SELECT EXISTS(SELECT 1 from like_post WHERE post_id = :postId and user_id = :userId limit 1)
                        """)
                .bind("postId", postId)
                .bind("userId", userId)
                .map(((row, rowMetadata) -> row.get(0, Boolean.class)))
                .one();
    }

}
