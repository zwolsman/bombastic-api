package com.zwolsman.bombastic.config

import com.zwolsman.bombastic.domain.PayOutOffer
import com.zwolsman.bombastic.domain.PointOffer

const val POINTS_PER_CENT = 0.002
const val pointsPerGame = 100

enum class PointsOffers(games: Int, x: Double) {
    SILVER(5, 0.005),
    GOLD(50, 0.0045),
    PLATINUM(100, 0.004),
    DIAMOND(250, 0.002);

    val offer = PointOffer(ordinal.toString(), name, games, x)
}

enum class PayOutOffers(price: Int) {
    REGULAR(1000);

    val offer = PayOutOffer(ordinal.toString(), name, price)
}
