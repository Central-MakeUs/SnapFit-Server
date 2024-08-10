package com.snapfit.main.common.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class PageResult <T> {
    private int offset;
    private int limit;

    private List<T> data;
}
