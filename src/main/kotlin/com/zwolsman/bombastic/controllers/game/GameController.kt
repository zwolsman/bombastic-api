package com.zwolsman.bombastic.controllers.game

import com.zwolsman.bombastic.controllers.game.response.GameDetailsResponse
import com.zwolsman.bombastic.controllers.game.response.GameResponse
import com.zwolsman.bombastic.controllers.game.response.GamesResponse
import com.zwolsman.bombastic.services.GameService
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
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
import java.security.Principal

@RestController
@RequestMapping("/api/v1/games")
class GameController(private val gameService: GameService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun create(@RequestBody payload: CreateGamePayload, principal: Principal): GameResponse {
        val (initialBet, bombs, colorId) = payload

        return gameService
            .create(owner = principal.name, initialBet, bombs, colorId)
            .let(::GameResponse)
    }

    @GetMapping("/{id}")
    suspend fun game(@PathVariable id: String): GameResponse {
        return gameService
            .byId(id)
            .let(::GameResponse)
    }

    @GetMapping("/{id}/details")
    suspend fun gameDetails(@PathVariable id: String): GameDetailsResponse {
        return gameService
            .byId(gameId = id)
            .let(::GameDetailsResponse)
    }

    @GetMapping
    suspend fun games(principal: Principal): GamesResponse {
        return gameService
            .allGames(owner = principal.name)
            .map(::GameResponse)
            .toList()
            .let(::GamesResponse)
    }

    @PutMapping("/{id}/guess")
    suspend fun guess(@PathVariable id: String, @RequestParam tileId: Int, principal: Principal): GameResponse {
        return gameService
            .guess(owner = principal.name, gameId = id, tileId)
            .let(::GameResponse)
    }

    @PutMapping("/{id}/cash-out")
    suspend fun cashOut(@PathVariable id: String, principal: Principal): GameResponse {
        return gameService
            .cashOut(owner = principal.name, gameId = id)
            .let(::GameResponse)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun illegalArgument(ex: IllegalArgumentException): String {
        return ex.message!!
    }
}
