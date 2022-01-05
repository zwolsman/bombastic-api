package com.zwolsman.bombastic.domain.converters

import com.zwolsman.bombastic.domain.Bomb
import com.zwolsman.bombastic.domain.Points
import com.zwolsman.bombastic.domain.Tile
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter

@WritingConverter
object TileWritingConverter : Converter<Tile, String> {
    override fun convert(source: Tile): String {
        val paddedId = source.id.toString().padStart(2, '0')
        val value = when (source) {
            is Bomb -> if (source.revealedByUser) {
                "BT"
            } else {
                "BF"
            }
            is Points -> "P${source.amount}"
        }
        return "$paddedId$value"
    }
}
