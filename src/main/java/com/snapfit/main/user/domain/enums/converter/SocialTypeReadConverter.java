package com.snapfit.main.user.domain.enums.converter;

import com.snapfit.main.user.domain.enums.SocialType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class SocialTypeReadConverter implements Converter<String, SocialType> {
    @Override
    public SocialType convert(String source) {

        return SocialType.findBySocial(source);
    }

}
