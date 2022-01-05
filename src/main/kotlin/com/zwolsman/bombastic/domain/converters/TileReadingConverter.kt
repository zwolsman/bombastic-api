package com.zwolsman.bombastic.domain.converters

import com.zwolsman.bombastic.domain.Bomb
import com.zwolsman.bombastic.domain.Points
import com.zwolsman.bombastic.domain.Tile
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
object TileReadingConverter : Converter<String, Tile> {
    override fun convert(source: String): Tile {
        val id = source.substring(0 until 2).toInt()
        return when (source[2]) {
            'B' -> Bomb(id, revealedByUser = source[3] == 'T')
            'P' -> Points(id, amount = source.substring(startIndex = 3).toInt())
            else -> throw Exception("Invalid tile state")
        }

    }
}