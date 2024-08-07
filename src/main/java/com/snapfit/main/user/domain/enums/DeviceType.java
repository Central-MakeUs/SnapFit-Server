package com.snapfit.main.user.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.snapfit.main.common.exception.ErrorResponse;
import com.snapfit.main.common.exception.enums.CommonErrorCode;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum DeviceType {
    APPLE("apple"),
    ANDROID("android");

    @JsonValue
    private final String device;

    DeviceType(String device) {
        this.device = device;
    }

    public static DeviceType findByDevice(String device) {
        return Arrays.stream(DeviceType.values()).filter(deviceType -> deviceType.device.equalsIgnoreCase(device))
                .findFirst()
                .orElseThrow(() ->new ErrorResponse(CommonErrorCode.INVALID_REQUEST));
    }
}
