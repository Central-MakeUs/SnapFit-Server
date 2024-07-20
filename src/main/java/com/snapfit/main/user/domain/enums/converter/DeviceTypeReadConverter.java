package com.snapfit.main.user.domain.enums.converter;

import com.snapfit.main.user.domain.enums.DeviceType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class DeviceTypeReadConverter implements Converter<String, DeviceType> {
    @Override
    public DeviceType convert(String source) {

        return DeviceType.findByDevice(source);
    }

}
