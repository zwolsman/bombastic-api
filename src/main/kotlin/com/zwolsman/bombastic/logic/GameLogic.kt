package com.zwolsman.bombastic.logic

import com.zwolsman.bombastic.domain.Bomb
import com.zwolsman.bombastic.domain.Game
import com.zwolsman.bombastic.domain.Points
import com.zwolsman.bombastic.domain.Profile
import com.zwolsman.bombastic.domain.Tile
import com.zwolsman.bombastic.helpers.validate
import java.security.SecureRandom
import kotlin.math.floor

object GameLogic {
    private val rng = SecureRandom()
    private val tileRange = 1..25
    const val houseEdge = 0.005
    private val allowedBombAmounts = listOf(1, 3, 5, 24)
    private const val alphanumeric = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

    fun createGame(profile: Profile, initialBet: Int, amountOfBombs: Int, colorId: Int): Game {
        validate(initialBet >= 100) { IllegalArgumentException("Minimum initial bet is 100 points") }
        validate(initialBet <= profile.points) { IllegalArgumentException("Not enough points") }
        validate(profile.id != null) { IllegalStateException("No ID for profile") }

        val secret = generateSecret(amountOfBombs)

        return Game(
            id = null,
            owner = profile.id,
            tiles = emptyList(),
            initialBet = initialBet,
            colorId = colorId,
            state = Game.State.IN_GAME,
            secret = secret,
            events = listOf(Game.Event.NewGame(initialBet, secret, bombs = amountOfBombs)),
            isDeleted = false,
        )
    }

    fun guess(game: Game, tileId: Int): Game {
        validate(game.state == Game.State.IN_GAME) { IllegalStateException("Game is already finished") }
        validate(tileId in tileRange) { IllegalArgumentException("Tile should be in the game") }
        validate(tileId !in game.tiles.map(Tile::id)) { IllegalArgumentException("Tile is already guessed") }

        val didHitBomb = tileId in game.bombs
        if (didHitBomb) {
            val tiles = game.bombs.filter { it != tileId }
                .map { Bomb(it, false) } +
                Bomb(tileId, true)

            return game.hitBomb(tiles, tileId)
        }

        validate(game.next != null) { IllegalStateException("Could not calculate next") }
        val points = Points(tileId, game.next)
        val event = Game.Event.Points(tileId, game.next)

        val updatedGame = game.copy(tiles = game.tiles + points, events = game.events + event)

        return if (updatedGame.hasTilesLeft)
            updatedGame
        else
            cashOut(updatedGame)
    }

    private fun Game.hitBomb(tiles: List<Tile>, tileId: Int): Game {
        return copy(
            tiles = this.tiles + tiles,
            events = events + Game.Event.Bomb(tileId, points = stake),
            state = Game.State.HIT_BOMB,
        )
    }

    private val Game.hasTilesLeft: Boolean
        get() = tiles.size + bombs.size + 1 < tileRange.last

    fun cashOut(game: Game): Game {
        validate(game.state == Game.State.IN_GAME) { IllegalStateException("Game is already finished") }

        return game.copy(
            tiles = game.tiles + game.bombs.map { Bomb(it, false) },
            events = game.events + Game.Event.CashOut(points = game.stake),
            state = Game.State.CASHED_OUT,
        )
    }

    fun calculateNext(game: Game): Int? {
        val tiles = tileRange.count().toDouble()
        val guessedTiles = game.tiles.filterIsInstance<Points>().size
        val bombs = game.bombs.size
        val tilesLeft = tiles - guessedTiles

        var multiplier = tilesLeft / (tilesLeft - bombs)
        multiplier *= 1 - houseEdge

        return when (multiplier) {
            Double.POSITIVE_INFINITY -> null
            else -> floor(game.stake * multiplier).toInt() - game.stake
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
