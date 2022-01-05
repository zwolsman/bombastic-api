package com.zwolsman.bombastic.controllers.game.response

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.zwolsman.bombastic.domain.converters.TileWritingConverter
import com.zwolsman.bombastic.domain.Game
import com.zwolsman.bombastic.domain.Tile
import org.springframework.boot.jackson.JsonComponent

class GameResponse(
    val id: String,
    val tiles: List<Tile>,
    val stake: Int,
    val next: Int,
    val state: Game.State,
)

@JsonComponent
class TileSerializer : JsonSerializer<Tile>() {
    private val converter = TileWritingConverter
    override fun serialize(value: Tile, gen: JsonGenerator, serializers: SerializerProvider) {
        val output = converter.convert(value)
        gen.writeString(output)
    }
}

fun GameResponse(game: Game): GameResponse = GameResponse(
    id = game.id.toString(),
    tiles = game.tiles,
    stake = game.stake,
    next = game.next,
    state = game.state
)