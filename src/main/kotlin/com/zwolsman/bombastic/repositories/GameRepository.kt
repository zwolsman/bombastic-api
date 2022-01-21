package com.zwolsman.bombastic.repositories

import com.zwolsman.bombastic.db.GameModel
import com.zwolsman.bombastic.domain.Game
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface GameDatabaseRepository : ReactiveCrudRepository<GameModel, String> {
    fun findAllByOwnerIdAndDeletedIsFalseOrderByIdDesc(ownerId: String): Flux<GameModel>
    fun findByIdAndDeletedIsFalse(id: String): Mono<GameModel>
}

@Repository
class GameRepository(private val db: GameDatabaseRepository) {
    suspend fun findById(id: String): Game? =
        db.findByIdAndDeletedIsFalse(id)
            .awaitFirstOrNull()
            ?.let(::Game)

    suspend fun findAll(ownerId: String): List<Game> =
        db
            .findAllByOwnerIdAndDeletedIsFalseOrderByIdDesc(ownerId)
            .asFlow()
            .map(::Game)
            .toList()

    @Transactional
    suspend fun save(game: Game): Game {
        return game
            .let(::GameModel)
            .let(db::save)
            .awaitFirst()
            .let(::Game)
    }
}
