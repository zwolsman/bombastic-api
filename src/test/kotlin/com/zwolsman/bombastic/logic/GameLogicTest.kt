package com.zwolsman.bombastic.logic

import com.zwolsman.bombastic.domain.Bomb
import com.zwolsman.bombastic.domain.Game
import com.zwolsman.bombastic.domain.Reveal
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class GameLogicTest {
    private fun createTestGame(bombs: Int? = null, secret: String? = null): Game {
        val dummySecret = bombs?.let { (1..bombs).joinToString(separator = "-") + "-secret" }
        val gameSecret = secret ?: dummySecret ?: error("No secret provided")

        return Game(
            id = null,
            owner = "",
            tiles = emptyList(),
            initialBet = 100,
            colorId = -1,
            state = Game.State.IN_GAME,
            secret = gameSecret,
            isDeleted = false,
        )
    }

    @Test
    fun `When no open tiles are left finish the game`() {
        val game = createTestGame(24)

        val result = GameLogic.guess(game, 25)

        assert(result.state == Game.State.CASHED_OUT)
        assert(result.next == null)
    }

    @Test
    fun `Should calculate next when there are tiles left`() {
        val game = createTestGame(3)

        val result = GameLogic.guess(game, 4)
        val tile = result.tiles.lastOrNull()

        assert(result.state == Game.State.IN_GAME)
        assert(result.next != null)
        assert(tile is Reveal)
    }

    @Test
    fun `Should not allow to guess a tile that is out of bounds`() {
        val game = createTestGame(3)

        val result = runCatching { GameLogic.guess(game, 69) }
        assert(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `Should not allow to guess a tile if the game is cashed out`() {
        val game = createTestGame(3).copy(state = Game.State.CASHED_OUT)

        val result = runCatching { GameLogic.guess(game, 5) }
        assert(result.exceptionOrNull() is IllegalStateException)
    }

    @Test
    fun `Should not allow to guess a tile if the game hit a bomb`() {
        val game = createTestGame(3).copy(state = Game.State.HIT_BOMB)

        val result = runCatching { GameLogic.guess(game, 5) }
        assert(result.exceptionOrNull() is IllegalStateException)
    }

    @Test
    fun `Should not allow to guess a tile if it has been guessed`() {
        val game = createTestGame(3)
        val firstGuess = GameLogic.guess(game, 4)
        val result = runCatching { GameLogic.guess(firstGuess, 4) }

        assert(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `Should reveal all bombs when hitting a bomb`() {
        val game = createTestGame(3)
        val next = game.next
        val result = GameLogic.guess(game, 3)
        val (tile1, tile2, tile3) = result.tiles.filterIsInstance<Bomb>()

        assert(!tile1.revealedByUser)
        assert(!tile2.revealedByUser)
        assert(tile3.revealedByUser)
        assert(result.state == Game.State.HIT_BOMB)
        assert(game.next == next)
    }

    @Test
    fun `Should reveal all bombs when cashing out`() {
        val game = createTestGame(3)
        val result = GameLogic.cashOut(game)
        val bombs = result.tiles.filterIsInstance<Bomb>()
        assert(result.state == Game.State.CASHED_OUT)
        assert(bombs.isNotEmpty())
        assert(bombs.all { !it.revealedByUser })
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 3, 5, 24])
    fun `Should have bombs+1 hyphens in the secret`(bombs: Int) {
        val game = createTestGame(secret = GameLogic.generateSecret(bombs))
        assert(game.bombs.size == bombs)

        val hyphenCount = game.secret.count { it == '-' }
        assert(hyphenCount == bombs)
    }

    @Test
    fun `Should only accept 1,3,5,24 as amount of bombs`() {
        val result = kotlin.runCatching { GameLogic.generateSecret(69) }

        assert(result.exceptionOrNull() is IllegalArgumentException)
    }
}
