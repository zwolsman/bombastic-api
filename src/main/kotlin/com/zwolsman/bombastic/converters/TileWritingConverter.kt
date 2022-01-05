package com.zwolsman.bombastic.converters

import com.zwolsman.bombastic.model.Bomb
import com.zwolsman.bombastic.model.Points
import com.zwolsman.bombastic.model.Tile
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class TileWritingConverter : Converter<Map<Int, Tile>, List<String>> {
    override fun convert(source: Map<Int, Tile>): List<String> {
        return source.map { (id, state) ->
            val value = when (state) {
                is Bomb -> if (state.revealedByUser) {
                    "BT"
                } else {
                    "BF"
                }
                is Points -> "P${state.amount}"
            }
            "${id.toString().padStart(2, '0')}$value"
        }
    }
}