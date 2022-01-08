package com.zwolsman.bombastic.controllers.store

import com.zwolsman.bombastic.config.PayOutOffers
import com.zwolsman.bombastic.config.PointsOffers
import com.zwolsman.bombastic.db.ProfileModel
import com.zwolsman.bombastic.domain.Offer
import com.zwolsman.bombastic.domain.PayOutOffer
import com.zwolsman.bombastic.domain.Profile
import com.zwolsman.bombastic.services.ProfileService
import org.springframework.stereotype.Service

@Service
class StoreService(private val profileService: ProfileService) {
    private val staticOffers = PointsOffers.values().map { it.offer } + PayOutOffers.values().map { it.offer }
    private val minimalPayOutAmount = 1000

    suspend fun personalOffers(profileId: String): List<Offer> {
        val offers = staticOffers + personalOffer(profileId)

        return offers.filterNotNull()
    }

    private suspend fun personalOffer(profileId: String): PayOutOffer? {
        val profile = profileService.findById(profileId)

        return if (profile.points >= minimalPayOutAmount)
            PayOutOffer("all", "EVERYTHING", profile.points)
        else
            null
    }

    suspend fun purchase(profileId: String, offerId: String): Profile {
        val offer = if (offerId == "pay-out-all")
            personalOffer(profileId)
        else
            staticOffers.firstOrNull { it.id == offerId }

        requireNotNull(offer) { "Offer has not been found" }

        return profileService
            .redeemOffer(profileId, offer)
    }
}
