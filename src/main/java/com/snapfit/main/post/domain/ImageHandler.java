package com.snapfit.main.post.domain;

import com.snapfit.main.post.domain.dto.PostFile;
import com.snapfit.main.post.domain.enums.FileType;

import java.util.List;

public interface ImageHandler {
    PostFile getImageSavePaths(List<FileType> fileTypes);
    Boolean isExistImage(String path);


}
