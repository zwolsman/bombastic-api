package com.zwolsman.bombastic.controllers

data class CreateGamePayload(
    val initialBet: Int,
    val bombs: Int,
    val colorId: Int,
)
