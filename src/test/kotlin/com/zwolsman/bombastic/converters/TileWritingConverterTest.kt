package com.zwolsman.bombastic.converters

import com.zwolsman.bombastic.model.Bomb
import com.zwolsman.bombastic.model.Points
import org.junit.jupiter.api.Test

internal class TileWritingConverterTest {

    private val converter = TileWritingConverter()

    @Test
    fun writeBomb() {
        val input = mapOf(1 to Bomb(false), 16 to Bomb(true))
        val result = converter.convert(input)

        assert(result.size == 2)
        assert(result[0] == "01BF")
        assert(result[1] == "16BT")
    }

    @Test
    fun writePoints() {
        val input = mapOf(1 to Points(100), 16 to Points(1337823))
        val result = converter.convert(input)

        assert(result.size == 2)
        assert(result[0] == "01P100")
        assert(result[1] == "16P1337823")
    }
}