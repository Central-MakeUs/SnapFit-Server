package com.snapfit.main.user.domain;

import com.snapfit.main.user.domain.enums.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("UserDevice")
@Builder
@Getter
@AllArgsConstructor
public class Device {
    @Id
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("device_id")
    private String deviceId;


    @Column("device_type")
    private DeviceType deviceType;

    private LocalDateTime loginDateTime;


}
