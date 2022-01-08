package com.zwolsman.bombastic.logic

import com.zwolsman.bombastic.domain.Game
import org.junit.jupiter.api.Test

internal class GameLogicTest {

    @Test
    fun `When no open tiles are left finish the game`() {
        val game = Game(
            id = null,
            owner = "",
            tiles = emptyList(),
            initialBet = 100,
            colorId = -1,
            state = Game.State.IN_GAME,
            secret = (1 until 25).joinToString(separator = "-") + "-secret"
        )

        val result = GameLogic.guess(game, 25)

        assert(result.state == Game.State.CASHED_OUT)
        assert(result.next == null)
    }
}
