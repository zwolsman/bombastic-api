package com.zwolsman.bombastic.repositories

import com.zwolsman.bombastic.db.ProfileModel
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface ProfileRepository : ReactiveCrudRepository<ProfileModel, Long> {
    fun findByAppleUserId(appleUserId: String): Mono<ProfileModel>
}
