package com.zwolsman.bombastic.repositories

import com.zwolsman.bombastic.db.GameModel
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface GameRepository : ReactiveCrudRepository<GameModel, String> {
    fun findAllByOwnerIdOrderByIdDesc(ownerId: String): Flux<GameModel>
}
