package com.zwolsman.bombastic.controllers.store

import com.zwolsman.bombastic.controllers.profile.ProfileResponse
import com.zwolsman.bombastic.logic.GameLogic
import com.zwolsman.bombastic.services.ProfileService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import kotlin.math.floor

@RestController
@RequestMapping("/api/v1/store")
class StoreController(private val profileService: ProfileService) {
    private val payOutOffer = object : Offer {
        override val points: Int = -1000
        override val price: Double = -4.0
    }

    @GetMapping("/offers")
    fun offers() = PointsPackages
        .values()
        .mapIndexed { index, pointsPackage ->
            PointsPackageResponse(
                (index + 1).toString(),
                pointsPackage.name,
                pointsPackage.price,
                pointsPackage.points,
                pointsPackage.bonus,
            )
        }
        .let(::StoreOfferResponse)

    @PostMapping("/offers/pay-out/purchase")
    suspend fun payOut(principal: Principal): ProfileResponse {
        return profileService
            .redeemOffer(principal.name, payOutOffer)
            .let(::ProfileResponse)
    }

    @PostMapping("/offers/{offerId}/purchase")
    suspend fun addPoints(@PathVariable offerId: Int, principal: Principal): ProfileResponse {
        val offer = PointsPackages.values()[offerId - 1]

        return profileService
            .redeemOffer(principal.name, offer)
            .let(::ProfileResponse)
    }
}

interface Offer {
    val points: Int
    val price: Double
}

class StoreOfferResponse(val offers: List<PointsPackageResponse>)
class PointsPackageResponse(val offerId: String, val name: String, val price: Double, val points: Int, val bonus: Int)

const val POINTS_PER_CENT = 0.002
const val pointsPerGame = 100

enum class PointsPackages(
    games: Int,
    x: Double
) : Offer {
    SILVER(5, 0.005),
    GOLD(50, 0.0045),
    PLATINUM(100, 0.004),
    DIAMOND(250, 0.002);

    private val multiplier = 1 + (GameLogic.houseEdge - x)
    private val basePoints = pointsPerGame * games
    val bonus = floor((multiplier * basePoints) - basePoints).toInt()

    override val points: Int = basePoints + bonus
    override val price = basePoints * POINTS_PER_CENT
}