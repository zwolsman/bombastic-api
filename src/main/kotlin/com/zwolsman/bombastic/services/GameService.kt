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
class GameService(
    private val profileService: ProfileService,
    private val gameRepository: GameRepository,
    private val gameLogic: GameLogic,
) {

    @Transactional
    suspend fun create(profile: Profile, initialBet: Int, amountOfBombs: Int, colorId: Int): Pair<Game, Profile> {
        val newGame = gameLogic.createGame(profile, initialBet, amountOfBombs, colorId)

        val game = gameRepository
            .save(newGame)

        val updatedProfile = profileService
            .createGame(profile, initialBet)

        return game to updatedProfile
    }

    suspend fun allGames(profile: Profile): List<Game> {
        validate(profile.id != null) { IllegalStateException("No ID for profile") }

        return gameRepository
            .findAll(ownerId = profile.id)
    }

    suspend fun byId(gameId: String): Game {
        val game = gameRepository.findById(gameId)
        validate(game != null) { Exception("Game not found") } // TODO: proper exception

        return game
    }

    @Transactional
    suspend fun guess(profile: Profile, gameId: String, tileId: Int): Pair<Game, Profile?> {
        val game = byId(gameId)
            .validateIsOwner(profile)
            .let { gameLogic.guess(it, tileId) }
            .let { gameRepository.save(it) }

        // Game has been automatically cashed out because there are no moves left anymore
        val updatedProfile = if (game.state == Game.State.CASHED_OUT)
            profileService.modifyPoints(profile, game.stake, game.earned)
        else
            null

        return game to updatedProfile
    }

    @Transactional
    suspend fun cashOut(profile: Profile, gameId: String): Pair<Game, Profile> {
        val game = byId(gameId)
            .validateIsOwner(profile)
            .let { gameLogic.cashOut(it) }
            .let { gameRepository.save(it) }

        val updatedProfile = profileService
            .modifyPoints(profile, game.stake, game.earned)

        return game to updatedProfile
    }

    suspend fun delete(profile: Profile, gameId: String) {
        val game = byId(gameId)
            .validateIsOwner(profile)
            .copy(isDeleted = true)

        gameRepository.save(game)
    }

    private fun Game.validateIsOwner(profile: Profile) = apply {
        validate(owner == profile.id) { SecurityAccessDeniedException("Denied") }
    }

    private val Game.earned: Int
        get() = stake - initialBet
}
