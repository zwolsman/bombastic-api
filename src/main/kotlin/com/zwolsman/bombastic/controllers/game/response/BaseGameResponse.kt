package com.zwolsman.bombastic.controllers.game.response

import com.zwolsman.bombastic.domain.Game
import com.zwolsman.bombastic.domain.Tile

interface BaseGameResponse {
    val id: String
    val tiles: List<Tile>
    val stake: Int
    val next: Int?
    val multiplier: Double
    val colorId: Int
    val state: Game.State
    val secret: String
    var bombs: Int
    var initialBet: Int
    val plain: String?
}
