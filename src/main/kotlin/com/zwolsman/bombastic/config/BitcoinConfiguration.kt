package com.zwolsman.bombastic.config

import org.bitcoinj.core.AbstractBlockChain
import org.bitcoinj.core.BlockChain
import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.core.PeerGroup
import org.bitcoinj.crypto.DeterministicKey
import org.bitcoinj.script.Script
import org.bitcoinj.store.BlockStore
import org.bitcoinj.store.SPVBlockStore
import org.bitcoinj.wallet.Wallet
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File
import java.util.concurrent.TimeUnit

@Configuration
class BitcoinConfiguration(
    @Value("\${btc.ppid}") private val paymentProtocolId: String,
    @Value("\${btc.wallet.watchKey.data}") private val watchKeyData: String,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun networkParameters(): NetworkParameters =
        NetworkParameters.fromPmtProtocolID(paymentProtocolId) ?: error("Unrecognized payment protocol id")

    @Bean
    fun blockStore(params: NetworkParameters) =
        SPVBlockStore(params, File("block-store-${params.paymentProtocolId}.dat"))

    @Bean
    fun wallet(params: NetworkParameters): Wallet {
        val file = File("wallet-${params.paymentProtocolId}.dat")
        log.info("Looking for wallet file ${file.absolutePath}")
        val wallet = if (file.exists()) {
            Wallet.loadFromFile(file)
                .also { log.info("Restoring wallet from file") }
        } else {
            val key = DeterministicKey.deserializeB58(watchKeyData, params)
            log.warn("No wallet found. Using the watching key to create one")
            Wallet.fromWatchingKey(params, key, Script.ScriptType.P2WPKH)
        }

        wallet.autosaveToFile(file, 1000, TimeUnit.MILLISECONDS, null)

        return wallet
    }

    @Bean
    fun peerGroup(params: NetworkParameters, wallet: Wallet, chain: AbstractBlockChain): PeerGroup {
        val peerGroup = PeerGroup(params, chain)
        peerGroup.addWallet(wallet)
        peerGroup.start()
        peerGroup.downloadBlockChain()
        log.info("Issued receive addresses: ${wallet.issuedReceiveAddresses.map { it.toString() }}")
        return peerGroup
    }

    @Bean
    fun chain(params: NetworkParameters, wallet: Wallet, blockStore: BlockStore) =
        BlockChain(params, wallet, blockStore)
}
