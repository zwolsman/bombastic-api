package com.zwolsman.bombastic.logic

import com.zwolsman.bombastic.domain.Bomb
import com.zwolsman.bombastic.domain.Game
import com.zwolsman.bombastic.domain.Points
import com.zwolsman.bombastic.domain.Profile
import com.zwolsman.bombastic.domain.Tile
import com.zwolsman.bombastic.domain.events.BombEvent
import com.zwolsman.bombastic.domain.events.CashOutEvent
import com.zwolsman.bombastic.domain.events.NewGameEvent
import com.zwolsman.bombastic.domain.events.PointsEvent
import com.zwolsman.bombastic.helpers.validate
import com.zwolsman.bombastic.repositories.EventService
import com.zwolsman.bombastic.repositories.GameRepository
import org.springframework.stereotype.Component
import java.security.SecureRandom
import kotlin.math.floor

@Component
class GameLogic(private val gameRepository: GameRepository, private val eventService: EventService) {
    companion object {
        private val rng = SecureRandom()
        private val tileRange = 1..25
        private val allowedBombAmounts = listOf(1, 3, 5, 24)
        private const val ALPHANUMERIC = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        const val HOUSE_EDGE = 0.005
        private const val MINIMUM_INITIAL_BET = 100

        fun calculateNext(game: Game): Int? {
            val tiles = tileRange.count().toDouble()
            val guessedTiles = game.tiles.filterIsInstance<Points>().size
            val bombs = game.bombs.size
            val tilesLeft = tiles - guessedTiles

            var multiplier = tilesLeft / (tilesLeft - bombs)
            multiplier *= 1 - HOUSE_EDGE

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
                .ints(length, 0, ALPHANUMERIC.length)
                .toArray()
                .map(ALPHANUMERIC::get)
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

    suspend fun createGame(profile: Profile, initialBet: Int, amountOfBombs: Int, colorId: Int): Game {
        validate(initialBet >= MINIMUM_INITIAL_BET) { IllegalArgumentException("Minimum initial bet is 100 points") }
        validate(initialBet <= profile.points) { IllegalArgumentException("Not enough points") }
        validate(profile.id != null) { IllegalStateException("No ID for profile") }

        val secret = generateSecret(amountOfBombs)

        val game = Game(
            id = null,
            owner = profile.id,
            tiles = emptyList(),
            initialBet = initialBet,
            colorId = colorId,
            state = Game.State.IN_GAME,
            secret = secret,
            isDeleted = false,
        )

        return gameRepository.save(game).also {
            eventService.save(NewGameEvent(it))
        }
    }

    suspend fun guess(game: Game, tileId: Int): Game {
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

        eventService.save(PointsEvent(points, game))
        val updatedGame = game.copy(tiles = game.tiles + points)

        return if (updatedGame.hasTilesLeft)
            updatedGame
        else
            cashOut(updatedGame)
    }

    private suspend fun Game.hitBomb(tiles: List<Tile>, tileId: Int): Game {
        eventService.save(BombEvent(tileId, this))

        return copy(
            tiles = this.tiles + tiles,
            state = Game.State.HIT_BOMB,
        )
    }

    private val Game.hasTilesLeft: Boolean
        get() = tiles.size + bombs.size + 1 < tileRange.last

    suspend fun cashOut(game: Game): Game {
        validate(game.state == Game.State.IN_GAME) { IllegalStateException("Game is already finished") }

        eventService.save(CashOutEvent(game))
        return game.copy(
            tiles = game.tiles + game.bombs.map { Bomb(it, false) },
            state = Game.State.CASHED_OUT,
        )
    }
}
