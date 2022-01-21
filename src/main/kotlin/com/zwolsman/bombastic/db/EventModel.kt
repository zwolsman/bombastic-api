package com.zwolsman.bombastic.db

import com.fasterxml.jackson.databind.ObjectMapper
import com.zwolsman.bombastic.domain.events.BombEvent
import com.zwolsman.bombastic.domain.events.CashOutEvent
import com.zwolsman.bombastic.domain.events.Event
import com.zwolsman.bombastic.domain.events.NewGameEvent
import com.zwolsman.bombastic.domain.events.PointsEvent
import io.r2dbc.postgresql.codec.Json
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

enum class EventType {
    NEW_GAME, POINTS, BOMB, CASH_OUT
}

@Table("events")
data class EventModel(
    val type: String,
    val metadata: Json? = null,

    @Id val id: Long? = null,
    val timestamp: LocalDateTime? = null,
)

fun EventModel(event: Event): EventModel {
    val type = when (event) {
        is BombEvent -> EventType.BOMB
        is CashOutEvent -> EventType.CASH_OUT
        is NewGameEvent -> EventType.NEW_GAME
        is PointsEvent -> EventType.POINTS
    }

    val metadata = ObjectMapper().writeValueAsString(event)

    return EventModel(
        id = event.id,
        type = type.name,
        metadata = Json.of(metadata),
    )
}
