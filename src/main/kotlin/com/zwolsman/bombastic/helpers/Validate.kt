package com.zwolsman.bombastic.helpers

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
inline fun validate(value: Boolean, lazyException: () -> Throwable) {
    contract {
        returns() implies value
    }
    if (!value) {
        throw lazyException()
    }
}