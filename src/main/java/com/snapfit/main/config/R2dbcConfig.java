package com.snapfit.main.config;

import com.snapfit.main.user.domain.enums.converter.SocialTypeReadConverter;
import com.snapfit.main.user.domain.enums.converter.SocialTypeWriteConverter;
import com.snapfit.main.user.domain.enums.converter.VibesReadConverter;
import com.snapfit.main.user.domain.enums.converter.VibesWriteConverter;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.DialectResolver;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.r2dbc.core.DatabaseClient;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableR2dbcAuditing
public class R2dbcConfig {

    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions(ConnectionFactory connectionFactory) {
        R2dbcDialect dialect = DialectResolver.getDialect(connectionFactory);

        var converters = new ArrayList<>(dialect.getConverters());
        converters.addAll(R2dbcCustomConversions.STORE_CONVERTERS);

        return new R2dbcCustomConversions(
                CustomConversions.StoreConversions.of(dialect.getSimpleTypeHolder(), converters),
                getCustomConverters());
    }

    private List<Object> getCustomConverters() {
        return List.of(new VibesReadConverter(), new VibesWriteConverter()
                , new SocialTypeReadConverter(), new SocialTypeWriteConverter());
    }
}
