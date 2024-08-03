package com.snapfit.main.user.infra.persistence.converter;

import com.snapfit.main.user.domain.enums.DeviceType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class DeviceTypeWriteConverter implements Converter<DeviceType, String> {
    @Override
    public String convert(DeviceType source) {

        return source.getDevice();
    }
}
