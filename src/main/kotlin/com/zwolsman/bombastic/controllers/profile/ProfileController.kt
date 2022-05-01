package com.zwolsman.bombastic.controllers.profile

import com.zwolsman.bombastic.controllers.profile.responses.ProfileResponse
import com.zwolsman.bombastic.controllers.profile.responses.TransactionResponse
import com.zwolsman.bombastic.controllers.profile.responses.TransactionType
import com.zwolsman.bombastic.controllers.profile.responses.TransactionsResponse
import com.zwolsman.bombastic.domain.Profile
import com.zwolsman.bombastic.helpers.toBits
import com.zwolsman.bombastic.services.ProfileService
import com.zwolsman.bombastic.services.TransactionService
import org.bitcoinj.core.NetworkParameters
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/profiles")
class ProfileController(
    private val profileService: ProfileService,
    private val transactionService: TransactionService,
    private val params: NetworkParameters,
) {

    @GetMapping("/me")
    suspend fun userProfile(@AuthenticationPrincipal profile: Profile): ProfileResponse =
        profile
            .let(::ProfileResponse)

    @GetMapping("/me/transactions")
    suspend fun userProfile(@AuthenticationPrincipal profile: Profile, unused: Int? = null): TransactionsResponse {
        val transactions = transactionService.findTransactions(profile)

        // TODO: create response mapper
        val transactionResponses = transactions.flatMap { tx ->
            tx.outputs.filter { output ->
                output.scriptPubKey.getToAddress(params).toString() == profile.address
            }
                .map { output ->
                    val amount = output.value.toBits()
                    TransactionResponse(
                        txId = tx.txId.toString(),
                        type = TransactionType.DEPOSIT,
                        amount = amount,
                        timestamp = tx.updateTime,
                        confirmed = tx.confidence.depthInBlocks >= 1
                    )
                }
        }
        return TransactionsResponse(
            name = profile.name,
            address = profile.address,
            transactions = transactionResponses
        )
    }

    @GetMapping("/{id}")
    suspend fun byId(@PathVariable id: String): ProfileResponse =
        profileService
            .findById(id)
            .let { ProfileResponse(it, exposeAddress = false) }
}
