package com.zwolsman.bombastic.controllers.profile.responses

import java.util.Date

data class TransactionsResponse(
    val name: String,
    val address: String,
    val transactions: List<TransactionResponse>,
)

data class TransactionResponse(
    val txId: String,
    val type: TransactionType,
    val amount: Long,
    val timestamp: Date,
    val confirmed: Boolean,
)

enum class TransactionType {
    DEPOSIT,
    WITHDRAW,
}
