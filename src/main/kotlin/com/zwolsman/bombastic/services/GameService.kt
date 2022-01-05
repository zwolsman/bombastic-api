package com.zwolsman.bombastic.services

import com.zwolsman.bombastic.db.GameModel
import com.zwolsman.bombastic.domain.Game
import com.zwolsman.bombastic.logic.GameLogic
import com.zwolsman.bombastic.repositories.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Service

@Service
class GameService(private val repo: GameRepository) {
    suspend fun create(initialBet: Int, amountOfBombs: Int, colorId: Int): Game {
        val model = GameModel(
            initialBet = initialBet,
            colorId = colorId,
            secret = GameLogic.generateSecret(amountOfBombs),
        )

        return repo
            .save(model)
            .awaitSingle()
            .let(::Game)
    }

    fun allGames(): Flow<Game> {
        return repo
            .findAll()
            .map(::Game)
            .asFlow()
    }

    suspend fun byId(gameId: String): Game {
        val model = repo.findById(gameId).awaitSingleOrNull() ?: throw GameNotFound(gameId)
        return model.let(::Game)
    }

    suspend fun guess(gameId: String, tileId: Int): Game {
        val model = byId(gameId)
            .let { GameLogic.guess(it, tileId) }
            .let(::GameModel)

        return repo
            .save(model)
            .awaitSingle()
            .let(::Game)
    }

    suspend fun cashOut(gameId: String): Game {
        val model = byId(gameId)
            .let { GameLogic.cashOut(it) }
            .let(::GameModel)

        return repo
            .save(model)
            .awaitSingle()
            .let(::Game)
    }
}

class GameNotFound(gameId: String) : Exception("Game not found with id '$gameId'")