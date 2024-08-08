package com.snapfit.main.post.application;


import com.snapfit.main.common.domain.location.Location;
import com.snapfit.main.common.domain.location.LocationFinder;
import com.snapfit.main.common.domain.vibe.Vibe;
import com.snapfit.main.common.domain.vibe.VibeFinder;
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
                .flatMap(post -> postRepository.save(post, request.getImageNames(), convertToLocations(request.getLocations()),convertToVibes(request.getVibes()), request.getPrices()))
                .map(this::convertToPostDetailDto);
    }


    private Post converToPost(CreatePostRequest postRequest, Long userId) {
        return Post.builder()
                .userId(userId)
                .title(postRequest.getTitle())
                .desc(postRequest.getDesc())
                .isStudio(postRequest.isStudio())
                .personPrice(postRequest.getPersonPrice())
                .thumbnail(postRequest.getThumbnail())
                .build();
    }

    private PostDetailDto convertToPostDetailDto(Post post) {
        return PostDetailDto.builder()
                .id(post.getId())
                .makerNickName(null)  // Replace with actual nickname if available
                .createAt(post.getCreateAt())
                .thumbnail(post.getThumbnail())
                .images(post.getPostImages().stream().map(PostImage::getPath).toList())
                .desc(post.getDesc())
                .title(post.getTitle())
                .personPrice(post.getPersonPrice())
                .vibes(post.getPostVibes().stream().map(Vibe::getName).toList())
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
}
