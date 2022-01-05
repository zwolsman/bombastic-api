package com.zwolsman.bombastic.db

import com.zwolsman.bombastic.domain.converters.TileWritingConverter
import com.zwolsman.bombastic.domain.Game
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("games")
data class GameModel(
    var initialBet: Int,
    var colorId: Int,
    var bombs: List<Int>,

    @Id
    var id: Long? = null,
    var tiles: List<String> = emptyList(),
    var cashedOut: Boolean = false,
)

fun GameModel(game: Game): GameModel {
    return GameModel(
        id = game.id,
        tiles = game.tiles.map(TileWritingConverter::convert),
        colorId = game.colorId,
        bombs = game.bombs,
        initialBet = game.initialBet,
        cashedOut = game.state == Game.State.CASHED_OUT
    )
}