package com.zwolsman.bombastic.services

import org.bitcoinj.core.Coin
import org.bitcoinj.core.Transaction
import org.bitcoinj.wallet.Wallet
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.Executor

@Service
class BitcoinService(
    private val executor: Executor,
    private val wallet: Wallet,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    init {
        wallet.addCoinsReceivedEventListener(executor, ::coinsReceivedEventListener)
    }

    private fun coinsReceivedEventListener(wallet: Wallet, tx: Transaction, coin: Coin, coin1: Coin) {
        val value: Coin = tx.getValueSentToMe(wallet)
        log.info("Received tx for ${value.toFriendlyString()}: $tx")
        log.info("Transaction will be approved after it confirms (1x).")

        tx.confidence.getDepthFuture(1).addListener(confirmedDepositEventListener(tx), executor)
    }

    private fun confirmedDepositEventListener(tx: Transaction) = Runnable {
        log.info("Confirmed deposit. txid: ${tx.txId}")
        for (output in tx.getWalletOutputs(wallet)) {
            val toAddress = output.scriptPubKey.getToAddress(tx.params)
            val amount = output.value.toSat()
            log.info("Received $amount bits on ${toAddress.hash}")

            TODO("Save bits for profile")
        }
    }
}
