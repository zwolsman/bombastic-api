package com.zwolsman.bombastic.controllers.game.payload

data class CreateGamePayload(
    val initialBet: Long,
    val bombs: Int,
    val colorId: Int,
)
