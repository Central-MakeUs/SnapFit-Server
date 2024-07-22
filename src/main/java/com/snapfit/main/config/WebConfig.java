package com.snapfit.main.config;

import com.snapfit.main.user.domain.enums.converter.SocialTypeReadConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class WebConfig implements WebFluxConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new SocialTypeReadConverter());
    }
}
