package com.zwolsman.bombastic.controllers.game.response

import com.zwolsman.bombastic.model.Game

class GameDetailsResponse(
    val id: String,
    val tiles: Map<Int, GameResponse.Tile>,
    val stake: Int,
    val next: Int,
    val multiplier: Double,
    val colorId: Int,
    val state: GameResponse.State,
    val secret: String,
    val plain: String?,
)

fun GameDetailsResponse(game: Game): GameDetailsResponse = TODO()