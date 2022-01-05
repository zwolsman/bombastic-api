package com.zwolsman.bombastic.converters

import com.zwolsman.bombastic.domain.Bomb
import com.zwolsman.bombastic.domain.Points
import com.zwolsman.bombastic.domain.converters.TileWritingConverter
import org.junit.jupiter.api.Test

internal class TileWritingConverterTest {
    @Test
    fun writeBomb() {
        val input = listOf(Bomb(1, false), Bomb(16, true))
        val expected = listOf("01BF", "16BT")
        for ((case, target) in input.zip(expected)) {
            val result = TileWritingConverter.convert(case)
            assert(result == target)
        }
    }

    @Test
    fun writePoints() {
        val input = listOf(Points(1, 100), Points(16, 1337823))
        val expected = listOf("01P100", "16P1337823")
        for ((case, target) in input.zip(expected)) {
            val result = TileWritingConverter.convert(case)
            assert(result == target)
        }
    }
}
