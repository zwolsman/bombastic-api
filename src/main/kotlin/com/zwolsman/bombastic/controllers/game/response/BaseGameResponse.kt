package com.zwolsman.bombastic.controllers.game.response

import com.zwolsman.bombastic.domain.Game
import com.zwolsman.bombastic.domain.Tile

interface BaseGameResponse {
    val id: String
    val tiles: List<Tile>
    val stake: Long
    val next: Long?
    val multiplier: Double
    val colorId: Int
    val state: Game.State
    val secret: String
    var bombs: Int
    var initialBet: Long
    val plain: String?
}
