package com.zwolsman.bombastic.services

import com.zwolsman.bombastic.db.GameModel
import com.zwolsman.bombastic.db.ProfileModel
import com.zwolsman.bombastic.domain.Game
import com.zwolsman.bombastic.logic.GameLogic
import com.zwolsman.bombastic.repositories.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GameService(private val profileService: ProfileService, private val gameRepository: GameRepository) {

    @Transactional
    suspend fun create(owner: String, initialBet: Int, amountOfBombs: Int, colorId: Int): Pair<Game, ProfileModel> {
        val model = GameModel(
            ownerId = owner,
            initialBet = initialBet,
            colorId = colorId,
            secret = GameLogic.generateSecret(amountOfBombs),
        )

        val game = gameRepository
            .save(model)
            .awaitSingle()
            .let(::Game)
        val profile = profileService.createGame(id = owner, initialBet)

        return game to profile
    }

    fun allGames(owner: String): Flow<Game> {
        return gameRepository
            .findAllByOwnerIdOrderByIdDesc(ownerId = owner)
            .map(::Game)
            .asFlow()
    }

    suspend fun byId(gameId: String): Game {
        val model = gameRepository.findById(gameId).awaitSingleOrNull() ?: throw GameNotFound(gameId)
        return model.let(::Game)
    }

    @Transactional
    suspend fun guess(owner: String, gameId: String, tileId: Int): Pair<Game, ProfileModel?> {
        val model = byId(gameId)
            .requireOwner(owner)
            .let { GameLogic.guess(it, tileId) }
            .let(::GameModel)

        val game = gameRepository
            .save(model)
            .awaitSingle()
            .let(::Game)

        // Game has been automatically cashed out because there are no moves left anymore
        val profile = if (game.state == Game.State.CASHED_OUT)
            profileService.modifyPoints(id = owner, game.stake, game.earned)
        else
            null

        return game to profile
    }

    @Transactional
    suspend fun cashOut(owner: String, gameId: String): Pair<Game, ProfileModel> {
        val model = byId(gameId)
            .requireOwner(owner)
            .let { GameLogic.cashOut(it) }
            .let(::GameModel)

        val game = gameRepository
            .save(model)
            .awaitSingle()
            .let(::Game)

        val profile = profileService.modifyPoints(id = owner, game.stake, game.earned)

        return game to profile
    }

    private fun Game.requireOwner(ownerId: String) = apply { require(owner == ownerId) }
    private val Game.earned: Int
        get() = stake - initialBet
}

class GameNotFound(gameId: String) : Exception("Game not found with id '$gameId'")
