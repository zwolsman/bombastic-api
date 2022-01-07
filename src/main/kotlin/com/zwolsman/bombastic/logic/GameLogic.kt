package com.zwolsman.bombastic.logic

import com.zwolsman.bombastic.domain.Bomb
import com.zwolsman.bombastic.domain.Game
import com.zwolsman.bombastic.domain.Points
import com.zwolsman.bombastic.domain.Tile
import java.security.SecureRandom
import kotlin.math.floor

object GameLogic {
    private val rng = SecureRandom()
    private val tileRange = 1..25
    private const val stitching = 0.005
    private val allowedBombAmounts = listOf(1, 3, 5, 24)
    private const val alphanumeric = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

    fun guess(game: Game, tileId: Int): Game {
        require(game.state == Game.State.IN_GAME) { "Game is already finished" }
        require(tileId in tileRange) { "Tile should be in the game" }
        require(tileId !in game.tiles.map(Tile::id)) { "Tile is already guessed" }

        val didHitBomb = tileId in game.bombs
        val result = if (didHitBomb)
            game.bombs.filter { it != tileId }
                .map { Bomb(it, false) } +
                Bomb(tileId, true)
        else
            listOf(Points(tileId, game.next))

        return game.copy(
            tiles = game.tiles + result,
            state = if (didHitBomb)
                Game.State.HIT_BOMB
            else
                Game.State.IN_GAME
        )
    }

    fun cashOut(game: Game): Game {
        require(game.state == Game.State.IN_GAME) { "Game is already finished" }
        return game.copy(
            state = Game.State.CASHED_OUT
        )
    }

    fun calculateNext(game: Game): Int {
        val guessedTiles = game.tiles.filterIsInstance<Points>().size
        var odds = (25 - guessedTiles) / (25.0 - guessedTiles - game.bombs.size)
        odds *= (1 - stitching)

        return floor(game.stake * odds).toInt() - game.stake
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
        require(amount in allowedBombAmounts) { "Allowed amounts of bombs: ${allowedBombAmounts.joinToString()}" }

        return generateSequence { tileRange.random(rng) }
            .distinct()
            .take(amount)
            .toSet()
    }

    private fun IntRange.random(random: SecureRandom): Int {
        return random.nextInt(endInclusive) + 1
    }
}
