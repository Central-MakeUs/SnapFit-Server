package com.snapfit.main.post.application;


import com.snapfit.main.common.domain.location.Location;
import com.snapfit.main.common.domain.location.LocationFinder;
import com.snapfit.main.common.domain.vibe.Vibe;
import com.snapfit.main.common.domain.vibe.VibeFinder;
import com.snapfit.main.common.dto.PageResult;
import com.snapfit.main.common.exception.ErrorResponse;
import com.snapfit.main.post.application.dto.PostDetailDto;
import com.snapfit.main.post.domain.*;
import com.snapfit.main.post.domain.dto.Price;
import com.snapfit.main.post.domain.exception.PostErrorCode;
import com.snapfit.main.post.presentation.dto.CreatePostRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final ImageHandler imageHandler;
    private final PostRepository postRepository;
    private final LocationFinder locationFinder;
    private final VibeFinder vibeFinder;

    public Mono<PostDetailDto> createPost(Long userId, CreatePostRequest request) {


        return Mono.just(request)
                .flatMap(req -> {
                    validImage(req.getImageNames());
                    validImage(List.of(req.getThumbnail()));
                    return Mono.just(req);
                })
                .map(req -> converToPost(req, userId))
                .flatMap(post -> postRepository.save(post,
                        request.getImageNames().stream().map(imageHandler::parseImagePath).toList(),
                        convertToLocations(request.getLocations()),
                        convertToVibes(request.getVibes()),
                        request.getPrices(), userId))
                .map(this::convertToPostDetailDto);
    }

    public Mono<Post> findPostById(long postId, long userId) {
        return postRepository.findById(postId, userId);
    }

    public Mono<PageResult<Post>> findByMaker(int limit, int offset, long makerId, long userId) {
        return postRepository.findByMaker(limit, offset, makerId, userId);
    }

    public Mono<PageResult<Post>> findByVibe( int limit, int offset, List<String> vibes, long userId) {
        return postRepository.findByVibes(limit, offset, vibes.stream().map(vibeFinder::findByVibe).toList(), userId);
    }

    public Mono<PageResult<Post>> find( int limit, int offset, long userId) {
        return postRepository.findAll(limit, offset, userId);
    }

    public Mono<Void> likePost(long userId, long postId) {
        return postRepository.likePost(userId, postId);
    }
    public Mono<Void> dislikePost(long userId, long postId) {
        return postRepository.disLikePost(userId, postId);
    }

    public Mono<Integer> countLikePost(long userId) {
        return postRepository.countLikePost(userId);
    }

    public Mono<PageResult<Post>> getLikePosts(int limit, int offset, long userId) {
        return postRepository.findLikePost(limit, offset, userId);
    }

    public Mono<Void> leaveMaker(long makerId) {
        return postRepository.leaveMaker(makerId);
    }

    private Post converToPost(CreatePostRequest postRequest, Long userId) {
        return Post.builder()
                .userId(userId)
                .title(postRequest.getTitle())
                .desc(postRequest.getDesc())
                .isStudio(postRequest.isStudio())
                .personPrice(postRequest.getPersonPrice())
                .thumbnail(imageHandler.parseImagePath(postRequest.getThumbnail()))
                .isValid(true)
                .build();
    }

    private PostDetailDto convertToPostDetailDto(Post post) {
        return PostDetailDto.builder()
                .id(post.getId())
                .createAt(post.getCreateAt())
                .thumbnail(post.getThumbnail())
                .images(post.getPostImages().stream().map(PostImage::getPath).toList())
                .desc(post.getDesc())
                .title(post.getTitle())
                .isLike(post.getIsLike())
                .personPrice(post.getPersonPrice())
                .vibes(post.getPostVibes().stream().map(Vibe::getName).toList())
                .isStudio(post.getIsStudio())
                .locations(post.getLocations().stream().map(Location::getAdminName).toList())
                .prices(post.getPostPrices().stream().map(postPrice -> new Price(postPrice.getMinute(), postPrice.getPrice())).toList())
                .build();
    }

    private void validImage(List<String> imagePath) {
        for (String image: imagePath) {
            if (!imageHandler.isExistImage(image)) {
                throw new ErrorResponse(PostErrorCode.NOT_EXIST_IMAGE);
            }
        };
    }

    private List<Location> convertToLocations(List<String> locations) {
        return locations.stream().map(locationFinder::findByAdminName).toList();
    }

    private List<Vibe> convertToVibes(List<String> vibes) {
        return vibes.stream().map(vibeFinder::findByVibe).toList();
    }

    private List<String> convertToImagePath(List<String> fileNames) {
        return fileNames.stream().map(imageHandler::parseImagePath).toList();
    }
}
