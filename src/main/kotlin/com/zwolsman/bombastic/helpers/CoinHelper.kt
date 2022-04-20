package com.zwolsman.bombastic.helpers

import org.bitcoinj.core.Coin

fun Coin.toBits(): Long = div(Coin.MICROCOIN.value).value
