package com.zwolsman.bombastic.config

import org.bitcoinj.core.AbstractBlockChain
import org.bitcoinj.core.BlockChain
import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.core.PeerGroup
import org.bitcoinj.crypto.DeterministicKey
import org.bitcoinj.net.discovery.DnsDiscovery
import org.bitcoinj.params.RegTestParams
import org.bitcoinj.script.Script
import org.bitcoinj.store.BlockStore
import org.bitcoinj.store.SPVBlockStore
import org.bitcoinj.wallet.Wallet
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File
import java.util.concurrent.TimeUnit

@Configuration
class BitcoinConfiguration(private val properties: Properties) {

    @ConfigurationProperties(prefix = "btc")
    @ConstructorBinding
    data class Properties(val ppid: String, val blockStore: BlockStoreProperties, val wallet: WalletProperties) {
        data class BlockStoreProperties(val type: String, val file: File)
        data class WalletProperties(val watchKey: String, val file: File)
    }

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun networkParameters(): NetworkParameters =
        NetworkParameters.fromPmtProtocolID(properties.ppid) ?: error("Unrecognized payment protocol id")

    @Bean
    @ConditionalOnProperty(
        value = ["btc.blockStore.type"],
        havingValue = "SPVBlockStore",
        matchIfMissing = false,
    )
    fun svpBlockStore(params: NetworkParameters) =
        SPVBlockStore(params, properties.blockStore.file)

    @Bean
    fun wallet(params: NetworkParameters): Wallet = with(properties.wallet) {
        log.info("Looking for wallet file ${file.absolutePath}")
        val wallet = if (file.exists()) {
            Wallet.loadFromFile(file)
                .also { log.info("Restoring wallet from file") }
        } else {
            val key = DeterministicKey.deserializeB58(watchKey, params)
            log.warn("No wallet found. Using the watching key to create one")

            Wallet
                .fromWatchingKey(params, key, Script.ScriptType.P2WPKH)
                .also(Wallet::reset)
        }

        wallet.autosaveToFile(file, 1000, TimeUnit.MILLISECONDS, null)

        return wallet
    }

    @Bean
    fun peerGroup(params: NetworkParameters, wallet: Wallet, chain: AbstractBlockChain): PeerGroup {
        val peerGroup = PeerGroup(params, chain)
        peerGroup.addWallet(wallet)
        if (params == RegTestParams.get())
            peerGroup.connectToLocalHost()
        else
            peerGroup.addPeerDiscovery(DnsDiscovery(params))

        peerGroup.isBloomFilteringEnabled = false
        peerGroup.maxConnections = 5
        peerGroup.start()
        peerGroup.downloadBlockChain()
        log.info("Issued receive addresses: ${wallet.issuedReceiveAddresses.map { it.toString() }}")
        return peerGroup
    }

    @Bean
    fun chain(params: NetworkParameters, wallet: Wallet, blockStore: BlockStore) =
        BlockChain(params, wallet, blockStore)
}
