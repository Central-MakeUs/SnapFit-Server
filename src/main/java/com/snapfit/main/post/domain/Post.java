package com.snapfit.main.post.domain;


import com.snapfit.main.common.domain.location.Location;
import com.snapfit.main.common.domain.vibe.Vibe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@Table("post")
@Builder
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Post {

    @Id
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("createAt")
    @CreatedDate
    private LocalDateTime createAt;

    @Column("is_studio")
    private Boolean isStudio;

    @Column("title")
    private String title;

    @Column("description")
    private String desc;

    @Column("person_price")
    private Integer personPrice;

    @Column("thumbnail")
    private String thumbnail;


    @Transient
    private List<PostPrice> postPrices;

    @Transient
    private List<Vibe> postVibes;

    @Transient
    private List<Location> locations;

    @Transient
    private List<PostImage> postImages;

    public void setId(Long id) {
        this.id = id;
    }

    public void setPostPrices(List<PostPrice> postPrices) {
        this.postPrices = postPrices;
    }

    public void setPostVibes(List<Vibe> postVibes) {
        this.postVibes = postVibes;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public void setPostImages(List<PostImage> postImages) {
        this.postImages = postImages;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
}
