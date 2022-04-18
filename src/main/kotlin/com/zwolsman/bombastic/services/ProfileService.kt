package com.zwolsman.bombastic.services

import com.zwolsman.bombastic.domain.Profile
import com.zwolsman.bombastic.helpers.validate
import com.zwolsman.bombastic.repositories.ProfileRepository
import org.bitcoinj.wallet.Wallet
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ProfileService(private val repo: ProfileRepository, private val wallet: Wallet) {
    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun createAppleUser(
        name: String,
        email: String,
        appleUserId: String,
        appleRefreshToken: String,
        appleAccessToken: String
    ): Profile {
        val profile = when (val appleUserProfile = repo.findByAppleUserId(appleUserId)) {
            null -> Profile(
                id = null,
                bits = 0,
                name = name,
                email = email,
                gamesPlayed = 0,
                bitsEarned = 0,
                appleUserId = appleUserId,
                appleRefreshToken = appleRefreshToken,
                appleAccessToken = appleAccessToken,
                address = wallet.freshReceiveAddress().toString(),
            )
            else -> appleUserProfile.copy(
                name = name,
                email = email,
                appleRefreshToken = appleRefreshToken,
                appleAccessToken = appleAccessToken,
            )
        }

        return repo
            .save(profile)
    }

    suspend fun findByAppleUserId(appleUserId: String): Profile =
        repo.findByAppleUserId(appleUserId) ?: throw Exception("Profile not found")

    suspend fun findById(id: String): Profile =
        repo.findById(id) ?: throw Exception("Profile not found")

    suspend fun modifyBits(profile: Profile, bits: Long, earned: Long): Profile {
        validate(bits >= 0) { IllegalArgumentException("Bits should be >= 0") }
        validate(earned >= 0) { IllegalArgumentException("Earned should be >= 0") }

        val updatedProfile = profile.copy(
            bits = profile.bits + bits,
            bitsEarned = profile.bitsEarned + earned,
        )
        return repo
            .save(updatedProfile)
    }

    suspend fun createGame(profile: Profile, initialBet: Long): Profile {
        val updatedProfile = profile.copy(
            bits = profile.bits - initialBet,
            gamesPlayed = profile.gamesPlayed + 1,
        )

        return repo
            .save(updatedProfile)
    }

    suspend fun addBits(address: String, bits: Long) {
        val profile = repo.findByAddress(address) ?: run {
            log.warn("No profile found associated with $address")
            return
        }

        log.info("Redeemed $bits bits for profile id ${profile.id} (${profile.name})")
        val updatedProfile = profile.copy(bits = profile.bits + bits)

        repo.save(updatedProfile)
    }
}
