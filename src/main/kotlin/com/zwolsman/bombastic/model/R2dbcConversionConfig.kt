package com.zwolsman.bombastic.model

import com.zwolsman.bombastic.converters.TileReadingConverter
import com.zwolsman.bombastic.converters.TileWritingConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.PostgresDialect

@Configuration
class R2dbcConversionConfig {
    @Bean
    @Override
    fun r2dbcCustomConversions(): R2dbcCustomConversions {
        return R2dbcCustomConversions.of(PostgresDialect.INSTANCE,
            listOf(TileWritingConverter(), TileReadingConverter()))
    }
}