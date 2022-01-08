package com.zwolsman.bombastic.controllers.game.response

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.zwolsman.bombastic.domain.Game
import com.zwolsman.bombastic.domain.Tile
import com.zwolsman.bombastic.domain.converters.TileWritingConverter
import org.springframework.boot.jackson.JsonComponent
import java.math.BigInteger
import java.security.MessageDigest

class GameResponse(
    val id: String,
    val tiles: List<Tile>,
    val stake: Int,
    val next: Int?,
    val multiplier: Double,
    val colorId: Int,
    val state: Game.State,
    val secret: String,
    var bombs: Int,
    var initialBet: Int,
    val plain: String?,
)

class GamesResponse(
    val games: List<GameResponse>
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
    multiplier = game.multiplier,
    colorId = game.colorId,
    state = game.state,
    bombs = game.bombs.size,
    initialBet = game.initialBet,
    secret = game.secret.sha256(),
    plain = game.state.takeIf { it == Game.State.IN_GAME }?.let { game.secret }
)

private fun String.sha256(): String {
    val md = MessageDigest.getInstance("SHA-256")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

private fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
}
