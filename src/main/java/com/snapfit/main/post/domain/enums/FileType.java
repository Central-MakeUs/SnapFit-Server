package com.snapfit.main.post.domain.enums;


import com.snapfit.main.common.exception.ErrorResponse;
import com.snapfit.main.common.exception.enums.CommonErrorCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum FileType {

    PNG("png", "image/png"),
    HEIC("heic", "image/heic"),
    HEIF("heif", "image/heif")
    ;
    private final String extension;
    private final String contentType;
    private final static Map<String, FileType> cache = new HashMap<>();

    static {
        for (FileType fileType : FileType.values()) {
            cache.put(fileType.getExtension(), fileType);
        }
    }

    FileType(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }


    public static FileType findByExtension(String extension) {
        FileType fileType = cache.get(extension);

        if (fileType == null) {
            throw new ErrorResponse(CommonErrorCode.INVALID_REQUEST);
        }

        return fileType;
    }

}
