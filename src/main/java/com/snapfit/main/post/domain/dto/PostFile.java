package com.snapfit.main.post.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostFile {

    private List<FileInfo> fileInfos;

    @Data
    @Builder
    public static class FileInfo {

        @Schema(description = "파일 이름")
        private String fileName;

        @Schema(description = "파일 업로드 되는 경로. 해당 경로에 이미지를 put 으로 넣어야 한다.")
        private String filePath;
    }
}
