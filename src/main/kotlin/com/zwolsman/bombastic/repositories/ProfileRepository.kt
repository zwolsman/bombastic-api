package com.zwolsman.bombastic.repositories

import com.zwolsman.bombastic.db.ProfileModel
import com.zwolsman.bombastic.domain.Profile
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface ProfileDatabaseRepository : ReactiveCrudRepository<ProfileModel, Long> {
    fun findByAppleUserId(appleUserId: String): Mono<ProfileModel>
}

@Repository
class ProfileRepository(private val db: ProfileDatabaseRepository) {

    suspend fun findById(id: String): Profile? =
        db
            .findById(id.toLong())
            .awaitFirstOrNull()
            ?.let(::Profile)

    suspend fun findByAppleUserId(appleUserId: String): Profile? =
        db
            .findByAppleUserId(appleUserId)
            .awaitFirstOrNull()
            ?.let(::Profile)

    suspend fun save(profile: Profile): Profile =
        profile
            .let(::ProfileModel)
            .let(db::save)
            .awaitFirst()
            .let(::Profile)
}