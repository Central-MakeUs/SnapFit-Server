package com.snapfit.main.post.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("post_price")
@Builder
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class PostPrice {

    @Id
    private Long id;

    @Column("post_id")
    private Long postId;

    @Column("minutes")
    private Integer minute;

    @Column("price")
    private Integer price;

}
