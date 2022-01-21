package com.zwolsman.bombastic.repositories

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.zwolsman.bombastic.db.EventModel
import com.zwolsman.bombastic.db.EventType
import com.zwolsman.bombastic.domain.events.BombEvent
import com.zwolsman.bombastic.domain.events.CashOutEvent
import com.zwolsman.bombastic.domain.events.Event
import com.zwolsman.bombastic.domain.events.NewGameEvent
import com.zwolsman.bombastic.domain.events.PointsEvent
import com.zwolsman.bombastic.helpers.validate
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface EventDatabaseRepository : ReactiveCrudRepository<EventModel, Long> {

    @Query("SELECT * FROM events WHERE metadata->>'gameId' = :gameId ORDER BY id DESC")
    fun findByGameId(gameId: Long): Flux<EventModel>
}

@Repository
class EventRepository(private val db: EventDatabaseRepository) {

    suspend fun save(event: Event): Event {
        return event
            .let(::EventModel)
            .let(db::save)
            .awaitFirst()
            .let(::Event)
    }

    suspend fun find(gameId: Long): List<Event> {
        return db
            .findByGameId(gameId)
            .asFlow()
            .map(::Event)
            .toList()
    }
}

fun Event(model: EventModel): Event {
    val metadata = model
        .metadata
        ?.let { ObjectMapper().readValue<Map<String, Any>>(it.asArray()) }
        .orEmpty()

    validate(model.id != null) { IllegalStateException("Event id is null") }
    return when (EventType.valueOf(model.type)) {
        EventType.NEW_GAME -> {
            val initialBet: Int by metadata
            val secret: String by metadata
            val bombs: Int by metadata

            NewGameEvent(initialBet, secret, bombs, model.id)
        }
        EventType.POINTS -> {
            val tileId: Int by metadata
            val amount: Int by metadata

            PointsEvent(tileId, amount, model.id)
        }
        EventType.BOMB -> {
            val tileId: Int by metadata
            val points: Int by metadata

            BombEvent(tileId, points, model.id)
        }
        EventType.CASH_OUT -> {
            val points: Int by metadata

            CashOutEvent(points, model.id)
        }
    }
}
