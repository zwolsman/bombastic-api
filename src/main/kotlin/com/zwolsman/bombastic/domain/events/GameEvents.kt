package com.zwolsman.bombastic.domain.events

import com.fasterxml.jackson.annotation.JsonIgnore
import com.zwolsman.bombastic.domain.Game
import com.zwolsman.bombastic.domain.Points as PointsTile

sealed interface GameEvent : Event {
    val gameId: Long
}

data class NewGameEvent(
    val initialBet: Int,
    val secret: String,
    val bombs: Int,
    override val gameId: Long,

    @JsonIgnore
    override val id: Long? = null,
) : GameEvent

data class PointsEvent(
    val tileId: Int,
    val amount: Int,
    override val gameId: Long,

    @JsonIgnore
    override val id: Long? = null,
) : GameEvent

data class BombEvent(
    val tileId: Int,
    val points: Int,
    override val gameId: Long,

    @JsonIgnore
    override val id: Long? = null,
) : GameEvent

data class CashOutEvent(
    val points: Int,
    override val gameId: Long,

    @JsonIgnore
    override val id: Long? = null,
) : GameEvent

fun NewGameEvent(game: Game): NewGameEvent {
    requireNotNull(game.id)
    return NewGameEvent(game.initialBet, game.secret, game.bombs.size, game.id)
}

fun PointsEvent(tile: PointsTile, game: Game): PointsEvent {
    requireNotNull(game.id)
    return PointsEvent(tile.id, tile.amount, game.id)
}

fun BombEvent(tileId: Int, game: Game): BombEvent {
    requireNotNull(game.id)
    return BombEvent(tileId, game.stake, game.id)
}

fun CashOutEvent(game: Game): CashOutEvent {
    requireNotNull(game.id)
    return CashOutEvent(game.stake, game.id)
}