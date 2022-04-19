package com.zwolsman.bombastic.services

import com.zwolsman.bombastic.domain.Profile
import org.bitcoinj.core.Transaction
import org.bitcoinj.wallet.Wallet
import org.springframework.stereotype.Service

@Service
class TransactionService(private val wallet: Wallet) {
    fun findTransactions(profile: Profile): List<Transaction> {
        return wallet.transactionsByTime.filter {
            it.outputs.any { output -> output.scriptPubKey.getToAddress(wallet.params).toString() == profile.address }
        }
    }
}
