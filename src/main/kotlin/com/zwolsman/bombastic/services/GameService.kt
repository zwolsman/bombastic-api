package com.zwolsman.bombastic.services

import com.zwolsman.bombastic.db.GameModel
import com.zwolsman.bombastic.domain.Game
import com.zwolsman.bombastic.domain.Profile
import com.zwolsman.bombastic.helpers.validate
import com.zwolsman.bombastic.logic.GameLogic
import com.zwolsman.bombastic.repositories.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.security.access.AccessDeniedException as SecurityAccessDeniedException

@Service
class GameService(private val profileService: ProfileService, private val gameRepository: GameRepository) {

    @Transactional
    suspend fun create(owner: String, initialBet: Int, amountOfBombs: Int, colorId: Int): Pair<Game, Profile> {
        validate(initialBet >= 100) { IllegalArgumentException("Minimum initial bet is 100 points") }
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
            .findAllByOwnerIdAndDeletedIsFalseOrderByIdDesc(ownerId = owner)
            .map(::Game)
            .asFlow()
    }

    suspend fun byId(gameId: String): Game {
        val model = gameRepository.findByIdAndDeletedIsFalse(gameId).awaitSingleOrNull()
        validate(model != null) { Exception("Game not found") } // TODO: proper exception

        return model.let(::Game)
    }

    @Transactional
    suspend fun guess(owner: String, gameId: String, tileId: Int): Pair<Game, Profile?> {
        val model = byId(gameId)
            .validateIsOwner(owner)
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
    suspend fun cashOut(owner: String, gameId: String): Pair<Game, Profile> {
        val model = byId(gameId)
            .validateIsOwner(owner)
            .let { GameLogic.cashOut(it) }
            .let(::GameModel)

        val game = gameRepository
            .save(model)
            .awaitSingle()
            .let(::Game)

        val profile = profileService.modifyPoints(id = owner, game.stake, game.earned)

        return game to profile
    }

    suspend fun delete(owner: String, gameId: String) {
        val model = byId(gameId)
            .validateIsOwner(owner)
            .let { GameModel(it.copy(isDeleted = true)) }

        gameRepository.save(model).awaitSingleOrNull()
    }

    private fun Game.validateIsOwner(ownerId: String) = apply {
        validate(owner == ownerId) { SecurityAccessDeniedException("Denied") }
    }

    private val Game.earned: Int
        get() = stake - initialBet
}
