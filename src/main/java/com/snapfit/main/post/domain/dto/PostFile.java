package com.snapfit.main.post.domain.dto;

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
        private String fileName;
        private String filePath;
    }
}
