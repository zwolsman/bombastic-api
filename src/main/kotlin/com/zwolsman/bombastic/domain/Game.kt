package com.zwolsman.bombastic.domain

import com.zwolsman.bombastic.db.GameModel
import com.zwolsman.bombastic.domain.converters.TileReadingConverter
import com.zwolsman.bombastic.logic.GameLogic

fun Game(model: GameModel): Game {
    val tiles = model.tiles.map(TileReadingConverter::convert)
    return Game(
        id = model.id,
        tiles = tiles,
        initialBet = model.initialBet,
        secret = model.secret,
        colorId = model.colorId,
        state = when {
            model.cashedOut -> Game.State.CASHED_OUT
            tiles.any { it is Bomb } -> Game.State.HIT_BOMB
            else -> Game.State.IN_GAME
        }
    )
}

data class Game(
    val id: Long?,
    val tiles: List<Tile>,
    val initialBet: Int,
    val colorId: Int,
    val state: State,
    val secret: String,
) {
    val stake = initialBet + tiles.filterIsInstance<Points>().sumOf { it.amount }
    val next = GameLogic.calculateNext(this)
    val bombs: List<Int>
        get() = secret.split("-").dropLast(1).map(String::toInt)

    enum class State {
        IN_GAME,
        CASHED_OUT,
        HIT_BOMB,
    }
}