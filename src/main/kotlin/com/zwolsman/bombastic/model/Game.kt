package com.zwolsman.bombastic.model

data class Game(
    val id: String,
    val tiles: Map<Int, Tile>,
    val stake: Int,
    val next: Int,
    val multiplier: Double,
    val colorId: Int,
    val state: State,
) {
    enum class State {
        IN_GAME,
        CASHED_OUT,
        HIT_BOMB,
    }

    sealed class Tile
    data class Points(val amount: Int) : Tile()
    data class Bomb(val revealedByUser: Boolean) : Tile()
}