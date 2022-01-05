package com.zwolsman.bombastic.logic

import com.zwolsman.bombastic.domain.Game
import com.zwolsman.bombastic.domain.Points
import java.security.SecureRandom
import kotlin.math.floor

object GameLogic {
    private val rng = SecureRandom()
    private val tileRange = 1..25
    private const val stitching = 0.005

    fun guess(game: Game, tileId: Int): Game {
        return game.copy(
            tiles = game.tiles + Points(tileId, game.next)
        )
    }

    fun cashOut(game: Game): Game {
        TODO()
    }

    fun calculateNext(game: Game): Int {
        val guessedTiles = game.tiles.filterIsInstance<Points>().size
        var odds = (25 - guessedTiles) / (25.0 - guessedTiles - game.bombs.size)
        odds *= (1 - stitching)

        return floor(game.stake * odds).toInt() - game.stake
    }

    fun generateBombs(amount: Int): List<Int> {

        require(amount in tileRange)
        require(amount < tileRange.last)

        return when (amount) {
            1 -> listOf(tileRange.random(rng))
            24 -> {
                val openTile = rng.nextInt(25) + 1
                tileRange.filter { it != openTile }
            }
            else -> {
                val bombs = mutableListOf<Int>()
                while (bombs.size < amount) {
                    val position = tileRange.random(rng)
                    if (position !in bombs)
                        bombs.add(position)
                }

                bombs
            }
        }
    }

    private fun IntRange.random(random: SecureRandom): Int {
        return random.nextInt(endInclusive) + 1
    }
}