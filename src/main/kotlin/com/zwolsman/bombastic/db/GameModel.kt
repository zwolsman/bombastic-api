package com.zwolsman.bombastic.db

import com.zwolsman.bombastic.domain.Game
import com.zwolsman.bombastic.domain.converters.TileWritingConverter
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("games")
data class GameModel(
    var initialBet: Long,
    var colorId: Int,
    var secret: String,

    @Id
    var id: Long? = null,
    var ownerId: String,
    var tiles: List<String> = emptyList(),
    var cashedOut: Boolean = false,
    var deleted: Boolean = false,
)

fun GameModel(game: Game): GameModel {
    return GameModel(
        id = game.id,
        ownerId = game.owner,
        tiles = game.tiles.map(TileWritingConverter::convert),
        colorId = game.colorId,
        secret = game.secret,
        initialBet = game.initialBet,
        cashedOut = game.state == Game.State.CASHED_OUT,
        deleted = game.isDeleted,
    )
}
