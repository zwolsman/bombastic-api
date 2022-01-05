package com.zwolsman.bombastic.converters

import com.zwolsman.bombastic.model.Bomb
import com.zwolsman.bombastic.model.Points
import org.junit.jupiter.api.Test

internal class TileReadingConverterTest {
    private val converter = TileReadingConverter()

    @Test
    fun readBomb() {
        val input = listOf("01BT", "16BF")
        val result = converter.convert(input)

        assert(result.containsKey(1))
        assert(result.containsKey(16))

        val b1 = result[1] as Bomb
        val b2 = result[16] as Bomb

        assert(b1.revealedByUser == true)
        assert(b2.revealedByUser == false)
    }

    @Test
    fun readPoints() {
        val input = listOf("01P100", "16P16543")
        val result = converter.convert(input)

        assert(result.containsKey(1))
        assert(result.containsKey(16))
        val p1 = result[1] as Points
        val p2 = result[16] as Points

        assert(p1.amount == 100)
        assert(p2.amount == 16543)
    }
}