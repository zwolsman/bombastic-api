package com.zwolsman.bombastic.controllers.store

import com.zwolsman.bombastic.controllers.profile.responses.ProfileResponse
import com.zwolsman.bombastic.controllers.store.response.OffersResponse
import com.zwolsman.bombastic.domain.Profile
import com.zwolsman.bombastic.services.StoreService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/store")
class StoreController(private val storeService: StoreService) {

    @GetMapping("/offers")
    suspend fun offers(@AuthenticationPrincipal profile: Profile): OffersResponse =
        storeService
            .personalOffers(profile)
            .let(::OffersResponse)

    @PostMapping("/offers/{offerId}/purchase")
    suspend fun addPoints(@PathVariable offerId: String, @AuthenticationPrincipal profile: Profile): ProfileResponse =
        storeService
            .purchase(profile, offerId = offerId)
            .let(::ProfileResponse)
}
