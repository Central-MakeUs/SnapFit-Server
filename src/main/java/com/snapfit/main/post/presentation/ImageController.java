package com.snapfit.main.post.presentation;

import com.snapfit.main.post.domain.ImageHandler;
import com.snapfit.main.post.domain.dto.PostFile;
import com.snapfit.main.post.domain.enums.FileType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class ImageController {

    //TODO 이미지 핸들러 adapter에서 이용하도록 수정.
    private final ImageHandler imageHandler;

    @GetMapping("/snapfit/image/paths")
    Mono<ResponseEntity<PostFile>> getImagePaths(@RequestParam("ext") List<String> exts) {
        return Mono.just(imageHandler.getImageSavePaths(exts.stream().map(FileType::findByExtension).toList()))
                .map(ResponseEntity::ok);
    }

}
