package com.zwolsman.bombastic.controllers.game

import com.zwolsman.bombastic.controllers.game.response.GameDetailsResponse
import com.zwolsman.bombastic.controllers.game.response.GameResponse
import com.zwolsman.bombastic.services.GameService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController("/v1/games")
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
    suspend fun games(): List<GameResponse> {
        TODO()
    }

    @GetMapping("{id}")
    suspend fun game(@PathVariable id: String): GameResponse {
        TODO()
    }

    @PutMapping("{id}/guess")
    suspend fun guess(@PathVariable id: String, @RequestParam tileId: Int): GameResponse {
        TODO()
    }

    @PutMapping("{id}/cash-out")
    suspend fun cashOut(@PathVariable id: String): GameResponse {
        TODO()
    }

    @GetMapping("{id}/details")
    suspend fun gameDetails(@PathVariable id: String): GameDetailsResponse {
        TODO()
    }
}
