package com.zwolsman.bombastic.repositories

import com.zwolsman.bombastic.db.GameModel
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface GameRepository : ReactiveCrudRepository<GameModel, String> {
    fun findAllByOwnerIdAndDeletedIsFalseOrderByIdDesc(ownerId: String): Flux<GameModel>
    fun findByIdAndDeletedIsFalse(id: String): Mono<GameModel>
}
