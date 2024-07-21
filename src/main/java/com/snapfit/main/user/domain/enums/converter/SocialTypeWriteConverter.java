package com.snapfit.main.user.domain.enums.converter;

import com.snapfit.main.user.domain.enums.SocialType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class SocialTypeWriteConverter implements Converter<SocialType, String> {
    @Override
    public String convert(SocialType source) {

        return source.getSocialName();
    }
}
