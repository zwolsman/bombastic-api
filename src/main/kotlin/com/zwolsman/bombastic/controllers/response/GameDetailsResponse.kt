package com.zwolsman.bombastic.controllers.response

import com.zwolsman.bombastic.model.Game

class GameDetailsResponse(
    id: String,
    tiles: Map<Int, Tile>,
    stake: Int,
    next: Int,
    multiplier: Double,
    colorId: Int,
    state: State,
    val secret: String,
    val plain: String?,
) : GameResponse(id, tiles, stake, next, multiplier, colorId, state)

fun GameDetailsResponse(game: Game): GameDetailsResponse = TODO()