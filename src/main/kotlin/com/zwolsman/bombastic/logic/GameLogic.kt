package com.zwolsman.bombastic.logic

import com.zwolsman.bombastic.domain.Bomb
import com.zwolsman.bombastic.domain.Game
import com.zwolsman.bombastic.domain.Reveal
import com.zwolsman.bombastic.domain.Tile
import com.zwolsman.bombastic.helpers.validate
import java.security.SecureRandom
import kotlin.math.floor

object GameLogic {
    private val rng = SecureRandom()
    private val tileRange = 1..25
    private const val houseEdge = 0.005
    private val allowedBombAmounts = listOf(1, 3, 5, 24)
    private const val alphanumeric = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

    fun guess(game: Game, tileId: Int): Game {
        validate(game.state == Game.State.IN_GAME) { IllegalStateException("Game is already finished") }
        validate(tileId in tileRange) { IllegalArgumentException("Tile should be in the game") }
        validate(tileId !in game.tiles.map(Tile::id)) { IllegalArgumentException("Tile is already revealed") }

        val didHitBomb = tileId in game.bombs
        if (didHitBomb) {
            val tiles = game.bombs.filter { it != tileId }
                .map { Bomb(it, false) } +
                Bomb(tileId, true)
            return game.hitBomb(tiles)
        }

        validate(game.next != null) { IllegalStateException("Could not calculate next") }
        val reveal = Reveal(tileId, game.next)

        if (game.tiles.size + game.bombs.size + 1 == tileRange.count()) {
            return cashOut(game.copy(tiles = game.tiles + reveal))
        }

        return game.copy(
            tiles = game.tiles + reveal
        )
    }

    private fun Game.hitBomb(tiles: List<Tile>): Game {
        return copy(
            tiles = this.tiles + tiles,
            state = Game.State.HIT_BOMB
        )
    }

    fun cashOut(game: Game): Game {
        validate(game.state == Game.State.IN_GAME) { IllegalStateException("Game is already finished") }
        return game.copy(
            state = Game.State.CASHED_OUT,
            tiles = game.tiles + game.bombs.map { Bomb(it, false) }
        )
    }

    fun calculateNext(game: Game): Long? {
        val tiles = tileRange.count().toDouble()
        val revealedTiles = game.tiles.filterIsInstance<Reveal>().size
        val bombs = game.bombs.size
        val tilesLeft = tiles - revealedTiles

        var multiplier = tilesLeft / (tilesLeft - bombs)
        multiplier *= 1 - houseEdge

        return when (multiplier) {
            Double.POSITIVE_INFINITY -> null
            else -> floor(game.stake * multiplier).toLong() - game.stake
        }
    }

    fun generateSecret(amountOfBombs: Int): String {
        val bombs = generateBombs(amountOfBombs).joinToString(separator = "-")
        val str = generateRandomString(16)

        return "$bombs-$str"
    }

    private fun generateRandomString(length: Long) =
        rng
            .ints(length, 0, alphanumeric.length)
            .toArray()
            .map(alphanumeric::get)
            .joinToString(separator = "")

    private fun generateBombs(amount: Int): Set<Int> {
        validate(amount in allowedBombAmounts) { IllegalArgumentException("Allowed amounts of bombs: ${allowedBombAmounts.joinToString()}") }

        return generateSequence { tileRange.random(rng) }
            .distinct()
            .take(amount)
            .toSet()
    }

    private fun IntRange.random(random: SecureRandom): Int {
        return random.nextInt(endInclusive) + 1
    }
}
