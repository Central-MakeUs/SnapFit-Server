package com.snapfit.main.user.domain;

import com.snapfit.main.user.domain.enums.DeviceType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("user_device")
@Builder
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
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

    public void updateLoginTime() {
        this.loginDateTime = LocalDateTime.now();
    }


}
