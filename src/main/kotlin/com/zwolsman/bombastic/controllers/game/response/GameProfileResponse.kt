package com.zwolsman.bombastic.controllers.game.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.zwolsman.bombastic.controllers.profile.responses.ProfileResponse
import com.zwolsman.bombastic.domain.Game
import com.zwolsman.bombastic.domain.Profile

@JsonInclude(JsonInclude.Include.NON_NULL)
class GameProfileResponse(gameResponse: GameResponse, val profile: ProfileResponse?) :
    BaseGameResponse by gameResponse

fun GameProfileResponse(pair: Pair<Game, Profile?>): GameProfileResponse {
    val gameResponse = pair.first.let(::GameResponse)
    val profileResponse = pair.second?.let(::ProfileResponse)
    return GameProfileResponse(gameResponse, profileResponse)
}
