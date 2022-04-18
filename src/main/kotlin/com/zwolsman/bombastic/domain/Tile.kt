package com.zwolsman.bombastic.domain

sealed class Tile(val id: Int)
class Points(id: Int, val amount: Long) : Tile(id)
class Bomb(id: Int, val revealedByUser: Boolean) : Tile(id)
