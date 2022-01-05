package com.zwolsman.bombastic.services

import com.zwolsman.bombastic.model.Game
import org.springframework.stereotype.Service

@Service
class GameService {

    fun create(initialBet: Int, amountOfBombs: Int, colorId: Int): Game {
        TODO()
    }

    fun allGames(): List<Game> {
        TODO()
    }

    fun byId(gameId: String): Game {
        TODO()
    }

    fun guess(gameId: String, tileId: Int): Game {
        TODO()
    }

    fun cashOut(gameId: String): Game {
        TODO()
    }
}

class GameNotFound(gameId: String) : Exception("Game not found with id '$gameId'")