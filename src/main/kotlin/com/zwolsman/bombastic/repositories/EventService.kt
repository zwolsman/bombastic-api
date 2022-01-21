package com.zwolsman.bombastic.repositories

import com.zwolsman.bombastic.domain.events.Event
import org.springframework.stereotype.Service

@Service
class EventService(private val repository: EventRepository) {
    suspend fun save(event: Event): Event {
        return repository.save(event)
    }
}
