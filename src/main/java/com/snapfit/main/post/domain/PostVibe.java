package com.snapfit.main.post.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("post_vibe")
@Builder
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class PostVibe {
    @Id
    private Long id;

    @Column("vibe_id")
    private Long vibeId;

    @Column("post_id")
    private Long postId;
}
