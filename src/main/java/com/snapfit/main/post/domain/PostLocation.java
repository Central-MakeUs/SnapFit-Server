package com.snapfit.main.post.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("post_location")
@Builder
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class PostLocation {
    @Id
    private Long id;

    @Column("post_id")
    private Long postId;

    @Column("location_id")
    private Long locationId;
}
