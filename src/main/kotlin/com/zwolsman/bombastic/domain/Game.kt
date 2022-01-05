package com.zwolsman.bombastic.domain

import com.zwolsman.bombastic.domain.converters.TileReadingConverter
import com.zwolsman.bombastic.db.GameModel
import com.zwolsman.bombastic.logic.GameLogic

fun Game(model: GameModel): Game {
    return Game(
        id = model.id,
        tiles = model.tiles.map(TileReadingConverter::convert),
        initialBet = model.initialBet,
        bombs = model.bombs,
        colorId = model.colorId,
        state = Game.State.IN_GAME
    )
}

data class Game(
    val id: Long?,
    val tiles: List<Tile>,
    val initialBet: Int,
    val colorId: Int,
    val state: State,
    val bombs: List<Int>,
) {
    val stake = initialBet + tiles.filterIsInstance<Points>().sumOf { it.amount }
    val next = GameLogic.calculateNext(this)

    enum class State {
        IN_GAME,
        CASHED_OUT,
        HIT_BOMB,
    }
}
