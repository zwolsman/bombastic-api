package com.zwolsman.bombastic.domain.events

sealed interface Event {
    val id: Long?
}
