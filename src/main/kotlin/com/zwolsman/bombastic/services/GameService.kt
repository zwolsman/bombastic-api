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
    suspend fun create(profile: Profile, initialBet: Long, amountOfBombs: Int, colorId: Int): Pair<Game, Profile> {
        validate(initialBet >= 100) { IllegalArgumentException("Minimum initial bet is 100 bits") }
        validate(initialBet <= profile.bits) { IllegalArgumentException("Not enough bits") }
        validate(profile.id != null) { IllegalStateException("No ID for profile") }

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
            .let { GameLogic.guess(it, tileId) }
            .let { gameRepository.save(it) }

        // Game has been automatically cashed out because there are no moves left anymore
        val updatedProfile = if (game.state == Game.State.CASHED_OUT)
            profileService.modifyBits(profile, game.stake, game.earned)
        else
            null

        return game to updatedProfile
    }

    @Transactional
    suspend fun cashOut(profile: Profile, gameId: String): Pair<Game, Profile> {
        val game = byId(gameId)
            .validateIsOwner(profile)
            .let { GameLogic.cashOut(it) }
            .let { gameRepository.save(it) }

        val updatedProfile = profileService
            .modifyBits(profile, game.stake, game.earned)

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

    private val Game.earned: Long
        get() = stake - initialBet
}
