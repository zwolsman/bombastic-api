package com.zwolsman.bombastic.services

import com.zwolsman.bombastic.domain.Game
import com.zwolsman.bombastic.domain.Profile
import com.zwolsman.bombastic.helpers.validate
import com.zwolsman.bombastic.logic.GameLogic
import com.zwolsman.bombastic.repositories.GameRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.security.access.AccessDeniedException as SecurityAccessDeniedException

@Service
class GameService(private val profileService: ProfileService, private val gameRepository: GameRepository) {

    @Transactional
    suspend fun create(profile: Profile, initialBet: Int, amountOfBombs: Int, colorId: Int): Pair<Game, Profile> {
        validate(initialBet >= 100) { IllegalArgumentException("Minimum initial bet is 100 points") }
        validate(initialBet <= profile.points) { IllegalArgumentException("Not enough points") }

        val newGame = Game(
            id = null,
            owner = profile.id,
            tiles = emptyList(),
            initialBet = initialBet,
            colorId = colorId,
            state = Game.State.IN_GAME,
            secret = GameLogic.generateSecret(amountOfBombs),
            isDeleted = false,
        )

        val game = gameRepository
            .save(newGame)
        val profile = profileService
            .createGame(id = profile.id, initialBet)

        return game to profile
    }

    suspend fun allGames(owner: String): List<Game> {
        return gameRepository
            .findAll(ownerId = owner)
    }

    suspend fun byId(gameId: String): Game {
        val game = gameRepository.findById(gameId)
        validate(game != null) { Exception("Game not found") } // TODO: proper exception

        return game
    }

    @Transactional
    suspend fun guess(owner: String, gameId: String, tileId: Int): Pair<Game, Profile?> {
        val game = byId(gameId)
            .validateIsOwner(owner)
            .let { GameLogic.guess(it, tileId) }
            .let { gameRepository.save(it) }

        // Game has been automatically cashed out because there are no moves left anymore
        val profile = if (game.state == Game.State.CASHED_OUT)
            profileService.modifyPoints(id = owner, game.stake, game.earned)
        else
            null

        return game to profile
    }

    @Transactional
    suspend fun cashOut(owner: String, gameId: String): Pair<Game, Profile> {
        val game = byId(gameId)
            .validateIsOwner(owner)
            .let { GameLogic.cashOut(it) }
            .let { gameRepository.save(it) }

        val profile = profileService.modifyPoints(id = owner, game.stake, game.earned)

        return game to profile
    }

    suspend fun delete(owner: String, gameId: String) {
        val game = byId(gameId)
            .validateIsOwner(owner)
            .copy(isDeleted = true)

        gameRepository.save(game)
    }

    private fun Game.validateIsOwner(ownerId: String) = apply {
        validate(owner == ownerId) { SecurityAccessDeniedException("Denied") }
    }

    private val Game.earned: Int
        get() = stake - initialBet
}
