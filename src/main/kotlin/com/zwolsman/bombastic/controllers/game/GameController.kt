package com.zwolsman.bombastic.controllers.game

import com.zwolsman.bombastic.controllers.game.payload.CreateGamePayload
import com.zwolsman.bombastic.controllers.game.response.GameProfileResponse
import com.zwolsman.bombastic.controllers.game.response.GameResponse
import com.zwolsman.bombastic.controllers.game.response.GamesResponse
import com.zwolsman.bombastic.domain.Profile
import com.zwolsman.bombastic.services.GameService
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/games")
class GameController(private val gameService: GameService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun create(
        @RequestBody payload: CreateGamePayload,
        @AuthenticationPrincipal profile: Profile
    ): GameProfileResponse {
        val (initialBet, bombs, colorId) = payload

        return gameService
            .create(profile, initialBet, bombs, colorId)
            .let(::GameProfileResponse)
    }

    @GetMapping("/{id}")
    suspend fun game(@PathVariable id: String): GameResponse =
        gameService
            .byId(id)
            .let(::GameResponse)

    @GetMapping
    suspend fun games(@AuthenticationPrincipal profile: Profile): GamesResponse =
        gameService
            .allGames(owner = profile.id)
            .map(::GameResponse)
            .let(::GamesResponse)

    @PutMapping("/{id}/guess")
    suspend fun guess(
        @PathVariable id: String,
        @RequestParam tileId: Int,
        @AuthenticationPrincipal profile: Profile
    ): GameProfileResponse =
        gameService
            .guess(owner = profile.id, gameId = id, tileId)
            .let(::GameProfileResponse)

    @PutMapping("/{id}/cash-out")
    suspend fun cashOut(@PathVariable id: String, @AuthenticationPrincipal profile: Profile): GameProfileResponse =
        gameService
            .cashOut(owner = profile.id, gameId = id)
            .let(::GameProfileResponse)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteGame(@PathVariable id: String, @AuthenticationPrincipal profile: Profile) =
        gameService
            .delete(owner = profile.id, gameId = id)
}
