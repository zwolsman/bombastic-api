package com.zwolsman.bombastic.domain

sealed class Tile(val id: Int)
class Points(id: Int, val amount: Int) : Tile(id)
class Bomb(id: Int, val revealedByUser: Boolean) : Tile(id)
