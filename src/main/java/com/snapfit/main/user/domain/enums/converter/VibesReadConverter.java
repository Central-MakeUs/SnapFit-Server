package com.snapfit.main.user.domain.enums.converter;

import com.snapfit.main.user.domain.enums.VibeType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.ArrayList;
import java.util.List;

@ReadingConverter
public class VibesReadConverter implements Converter<String, List<VibeType>> {
    @Override
    public List<VibeType> convert(String input) {
        List<VibeType> result = new ArrayList<>();

        List<String> parsingVibes = List.of(input.split(","));

        parsingVibes.forEach(vibe -> result.add(VibeType.findByVibe(vibe)));

        return result;
    }

}
