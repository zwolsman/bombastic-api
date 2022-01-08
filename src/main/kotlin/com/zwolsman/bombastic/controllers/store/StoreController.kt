package com.zwolsman.bombastic.controllers.store

import com.zwolsman.bombastic.controllers.profile.ProfileResponse
import com.zwolsman.bombastic.controllers.store.response.OffersResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/v1/store")
class StoreController(private val storeService: StoreService) {

    @GetMapping("/offers")
    suspend fun offers(principal: Principal): OffersResponse {
        return storeService
            .personalOffers(profileId = principal.name)
            .let(::OffersResponse)
    }

    @PostMapping("/offers/{offerId}/purchase")
    suspend fun addPoints(@PathVariable offerId: String, principal: Principal): ProfileResponse {
        return storeService
            .purchase(profileId = principal.name, offerId = offerId)
            .let(::ProfileResponse)
    }
}
