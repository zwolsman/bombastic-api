package com.zwolsman.bombastic.controllers.game.response

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.zwolsman.bombastic.domain.Tile
import com.zwolsman.bombastic.domain.converters.TileWritingConverter
import org.springframework.boot.jackson.JsonComponent

@JsonComponent
class TileSerializer : JsonSerializer<Tile>() {
    override fun serialize(value: Tile, gen: JsonGenerator, serializers: SerializerProvider) {
        val output = TileWritingConverter.convert(value)
        gen.writeString(output)
    }
}
