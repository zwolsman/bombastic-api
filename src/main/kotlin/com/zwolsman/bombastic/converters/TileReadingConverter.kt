package com.zwolsman.bombastic.converters

import com.zwolsman.bombastic.model.Bomb
import com.zwolsman.bombastic.model.Points
import com.zwolsman.bombastic.model.Tile
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class TileReadingConverter : Converter<List<String>, Map<Int, Tile>> {
    override fun convert(source: List<String>): Map<Int, Tile> {
        return source.associate {
            val id = it.substring(0 until 2).toInt()
            val state = when (it[2]) {
                'B' -> Bomb(revealedByUser = it[3] == 'T')
                'P' -> Points(amount = it.substring(startIndex = 3).toInt())
                else -> throw Exception("Invalid tile state")
            }
            id to state
        }
    }
}