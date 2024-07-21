package com.snapfit.main.user.domain.enums.converter;

import com.snapfit.main.user.domain.enums.VibeType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.util.List;

@WritingConverter
public class VibesWriteConverter implements Converter<List<VibeType>, String> {
    @Override
    public String convert(List<VibeType> source) {

        return String.join(",", source.stream().map(VibeType::getVibe).toList());
    }
}
