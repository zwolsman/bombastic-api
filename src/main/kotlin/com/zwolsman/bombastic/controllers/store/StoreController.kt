package com.zwolsman.bombastic.controllers.store

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import kotlin.math.floor

@RestController
@RequestMapping("/api/v1/store")
class StoreController {

    @GetMapping("/offers")
    fun offers() = PointsPackages
        .values()
        .mapIndexed { index, pointsPackage ->
            PointsPackageResponse(
                index + 1,
                pointsPackage.name,
                pointsPackage.euros,
                pointsPackage.points,
                pointsPackage.bonus,
            )
        }
        .let(::StoreOfferResponse)

    @PostMapping("/buy")
    fun addPoints(@RequestParam offerId: Int, principal: Principal) {
    }
}

class StoreOfferResponse(val offers: List<PointsPackageResponse>)
class PointsPackageResponse(val offerId: Int, val name: String, val price: Double, val points: Int, val bonus: Int)

const val POINTS_PER_CENT = 0.002

const val houseEdge = 0.005
const val pointsPerGame = 100

enum class PointsPackages(
    games: Int,
    x: Double
) {
    SILVER(5, 0.005),
    GOLD(50, 0.0045),
    PLATINUM(100, 0.004),
    DIAMOND(250, 0.002);

    private val multiplier = 1 + (houseEdge - x)
    private val basePoints = pointsPerGame * games
    val bonus = floor((multiplier * basePoints) - basePoints).toInt()

    val points: Int = basePoints + bonus
    val euros = basePoints * POINTS_PER_CENT
}