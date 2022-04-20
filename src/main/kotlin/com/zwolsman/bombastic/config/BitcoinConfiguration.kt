package com.zwolsman.bombastic.config

import org.bitcoinj.core.AbstractBlockChain
import org.bitcoinj.core.BlockChain
import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.core.PeerGroup
import org.bitcoinj.core.UTXOProvider
import org.bitcoinj.crypto.DeterministicKey
import org.bitcoinj.script.Script
import org.bitcoinj.store.BlockStore
import org.bitcoinj.store.PostgresFullPrunedBlockStore
import org.bitcoinj.wallet.Wallet
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

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
    fun blockStore(params: NetworkParameters) = SPVBlockStore(params, File("block-store-${params.paymentProtocolId}.dat"))

    @Bean
    fun wallet(params: NetworkParameters, utxoProvider: UTXOProvider): Wallet {
        val key = DeterministicKey.deserializeB58(watchKeyData, params)
        val wallet = Wallet.fromWatchingKey(params, key, Script.ScriptType.P2PKH)
        wallet.utxoProvider = utxoProvider
        return wallet
    }

    @Bean
    fun peerGroup(params: NetworkParameters, wallet: Wallet, chain: AbstractBlockChain): PeerGroup {
        val peerGroup = PeerGroup(params, chain)
        peerGroup.addWallet(wallet)
        peerGroup.start()
        peerGroup.downloadBlockChain()
        log.info("Addresses that we know: ${wallet.issuedReceiveAddresses.map { it.hash }}")
        log.info("Address: ${wallet.freshReceiveAddress()}")
        return peerGroup
    }

    @Bean
    fun chain(params: NetworkParameters, wallet: Wallet, blockStore: BlockStore) =
        BlockChain(params, wallet, blockStore)
}
