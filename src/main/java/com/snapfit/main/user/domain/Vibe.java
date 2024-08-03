package com.snapfit.main.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table
@Builder
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Vibe {

    @Id
    private Long id;

    //TODO 추후 분위기마다 사진 예시 추가 예정
//    private String path;

    private String name;

}
