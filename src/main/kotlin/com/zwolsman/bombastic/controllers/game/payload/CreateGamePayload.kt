package com.zwolsman.bombastic.controllers.game.payload

data class CreateGamePayload(
    val initialBet: Int,
    val bombs: Int,
    val colorId: Int,
)
