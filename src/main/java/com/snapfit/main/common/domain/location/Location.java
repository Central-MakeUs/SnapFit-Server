package com.snapfit.main.common.domain.location;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("location_config")
@Builder
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Location {
    @Id
    private Long id;

    @Column("admin_name")
    private String adminName;
}
