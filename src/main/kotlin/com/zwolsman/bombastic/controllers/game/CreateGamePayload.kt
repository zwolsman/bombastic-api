package com.zwolsman.bombastic.controllers.game

data class CreateGamePayload(
    val initialBet: Int,
    val bombs: Int,
    val colorId: Int,
)
