package com.zwolsman.bombastic.services

import com.zwolsman.bombastic.config.PayOutOffers
import com.zwolsman.bombastic.config.PointsOffers
import com.zwolsman.bombastic.domain.Offer
import com.zwolsman.bombastic.domain.PayOutOffer
import com.zwolsman.bombastic.domain.Profile
import com.zwolsman.bombastic.helpers.validate
import org.springframework.stereotype.Service

@Service
class StoreService(private val profileService: ProfileService) {
    private val staticOffers = PointsOffers.values().map { it.offer } + PayOutOffers.values().map { it.offer }
    private val minimalPayOutAmount = 1000

    suspend fun personalOffers(profile: Profile): List<Offer> {
        validate(profile.id != null) { IllegalStateException("No ID for profile") }
        val offers = staticOffers + personalOffer(profile.id)

        return offers.filterNotNull()
    }

    private suspend fun personalOffer(profileId: String): PayOutOffer? {
        val profile = profileService.findById(profileId)

        return if (profile.points >= minimalPayOutAmount)
            PayOutOffer("all", "EVERYTHING", profile.points)
        else
            null
    }

    suspend fun purchase(profile: Profile, offerId: String): Profile {
        val offer = if (offerId == "pay-out-all" && profile.id != null)
            personalOffer(profile.id)
        else
            staticOffers.firstOrNull { it.id == offerId }

        validate(offer != null) { IllegalArgumentException("Offer has not been found") }

        return profileService
            .redeemOffer(profile, offer)
    }
}
