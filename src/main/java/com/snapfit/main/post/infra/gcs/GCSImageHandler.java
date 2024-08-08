package com.snapfit.main.post.infra.gcs;

import com.google.cloud.storage.*;
import com.snapfit.main.post.domain.ImageHandler;
import com.snapfit.main.post.domain.dto.PostFile;
import com.snapfit.main.post.domain.enums.FileType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class GCSImageHandler implements ImageHandler {
    private final Storage storage;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    @Value("${spring.cloud.gcp.storage.max-size}")
    private String maxSize;
    @Override
    public PostFile getImageSavePaths(List<FileType> fileTypes) {
        List<PostFile.FileInfo> fileInfos = new ArrayList<>();

        fileTypes.forEach(image ->{
            String fileName = UUID.randomUUID().toString()+ "." + image.getExtension();
            BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, fileName)).build();

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-type", image.getContentType());
            //TODO 이미지 사이즈 제한 필요.
//            headers.put("Content-Length", maxSize);

            URL url = storage.signUrl(blobInfo, 3, TimeUnit.MINUTES,
                    Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
                    Storage.SignUrlOption.withExtHeaders(headers),
                    Storage.SignUrlOption.withV4Signature());

            fileInfos.add(PostFile.FileInfo.builder()
                    .fileName(fileName)
                    .filePath(url.toString())
                    .build());
        });


        return PostFile.builder()
                .fileInfos(fileInfos)
                .build();
    }

    @Override
    public Boolean isExistImage(String fileName) {
        Blob blob = storage.get(bucketName, fileName);

        return blob != null && blob.exists();
    }
}
