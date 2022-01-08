package com.zwolsman.bombastic.domain

import com.zwolsman.bombastic.config.POINTS_PER_CENT
import com.zwolsman.bombastic.config.pointsPerGame
import com.zwolsman.bombastic.logic.GameLogic
import kotlin.math.floor

sealed class Offer(val id: String, val name: String) {
    abstract val currency: Currency
    abstract val price: Double
    abstract val reward: Double

    enum class Currency {
        MONEY,
        POINTS
    }
}

class PayOutOffer(id: String, name: String, price: Int) : Offer("pay-out-$id", name) {
    override val currency = Currency.POINTS
    override val price = price.toDouble()
    override val reward = price * POINTS_PER_CENT
}

class PointOffer(id: String, name: String, games: Int, x: Double) : Offer("points-$id", name) {
    override val currency = Currency.MONEY
    private val multiplier = 1 + (GameLogic.houseEdge - x)
    private val basePoints = pointsPerGame * games
    val bonus = floor((multiplier * basePoints) - basePoints)

    override val reward = basePoints + bonus
    override val price = basePoints * POINTS_PER_CENT
}
