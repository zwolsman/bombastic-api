package com.zwolsman.bombastic.controllers.game.response

import com.zwolsman.bombastic.domain.Game
import com.zwolsman.bombastic.domain.Tile
import java.math.BigInteger
import java.security.MessageDigest

class GameResponse(
    override val id: String,
    override val tiles: List<Tile>,
    override val stake: Long,
    override val next: Long?,
    override val multiplier: Double,
    override val colorId: Int,
    override val state: Game.State,
    override val secret: String,
    override var bombs: Int,
    override var initialBet: Long,
    override val plain: String?,
) : BaseGameResponse

fun GameResponse(game: Game): GameResponse = GameResponse(
    id = game.id.toString(),
    tiles = game.tiles,
    stake = game.stake,
    next = game.next,
    multiplier = "%.2f".format(game.multiplier).toDouble(),
    colorId = game.colorId,
    state = game.state,
    bombs = game.bombs.size,
    initialBet = game.initialBet,
    secret = game.secret.sha256(),
    plain = game.state.takeIf { it != Game.State.IN_GAME }?.let { game.secret }
)

private fun String.sha256(): String {
    val md = MessageDigest.getInstance("SHA-256")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}
