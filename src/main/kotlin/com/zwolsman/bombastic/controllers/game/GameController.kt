package com.zwolsman.bombastic.controllers.game

import com.zwolsman.bombastic.controllers.game.response.GameDetailsResponse
import com.zwolsman.bombastic.controllers.game.response.GameResponse
import com.zwolsman.bombastic.services.GameService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
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
@RequestMapping("/v1/games")
class GameController(private val gameService: GameService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun create(@RequestBody payload: CreateGamePayload): GameResponse {
        val (initialBet, bombs, colorId) = payload

        return gameService
            .create(initialBet, bombs, colorId)
            .let(::GameResponse)
    }

    @GetMapping
    fun games(): Flow<GameResponse> {
        return gameService
            .allGames()
            .map(::GameResponse)
    }

    @GetMapping("{id}")
    suspend fun game(@PathVariable id: String): GameResponse {
        return gameService
            .byId(id)
            .let(::GameResponse)
    }

    @PutMapping("{id}/guess")
    suspend fun guess(@PathVariable id: String, @RequestParam tileId: Int): GameResponse {
        return gameService
            .guess(gameId = id, tileId)
            .let(::GameResponse)
    }

    @PutMapping("{id}/cash-out")
    suspend fun cashOut(@PathVariable id: String): GameResponse {
        return gameService
            .cashOut(gameId = id)
            .let(::GameResponse)
    }

    @GetMapping("{id}/details")
    suspend fun gameDetails(@PathVariable id: String): GameDetailsResponse {
        return gameService
            .byId(gameId = id)
            .let(::GameDetailsResponse)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun illegalArgument(ex: IllegalArgumentException): String {
        return ex.message!!
    }
}
