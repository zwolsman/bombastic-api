package com.zwolsman.bombastic.domain

sealed class Tile(val id: Int)
class Reveal(id: Int, val bits: Long) : Tile(id)
class Bomb(id: Int, val revealedByUser: Boolean) : Tile(id)
