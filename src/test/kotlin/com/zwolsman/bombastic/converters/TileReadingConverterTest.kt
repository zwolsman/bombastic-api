package com.zwolsman.bombastic.converters

import com.zwolsman.bombastic.domain.Bomb
import com.zwolsman.bombastic.domain.Reveal
import com.zwolsman.bombastic.domain.converters.TileReadingConverter
import org.junit.jupiter.api.Test

internal class TileReadingConverterTest {
    @Test
    fun readBomb() {
        val input = listOf("01BT", "16BF")
        val expected = listOf(Bomb(1, true), Bomb(16, false))
        for ((case, target) in input.zip(expected)) {
            val result = TileReadingConverter.convert(case)
            require(result is Bomb)
            assert(result.id == target.id)
            assert(result.revealedByUser == target.revealedByUser)
        }
    }

    @Test
    fun readReveal() {
        val input = listOf("01P100", "16P154671")
        val expected = listOf(Reveal(1, 100), Reveal(16, 154671))
        for ((case, target) in input.zip(expected)) {
            val result = TileReadingConverter.convert(case)
            require(result is Reveal)
            assert(result.id == target.id)
            assert(result.bits == target.bits)
        }
    }
}
