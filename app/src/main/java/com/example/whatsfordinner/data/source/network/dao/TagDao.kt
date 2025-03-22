package com.example.whatsfordinner.data.source.network.dao

import com.example.whatsfordinner.dto.DishDto
import com.example.whatsfordinner.dto.TagDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class TagDao (
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("type") val type: TagType,
    @SerialName("subtype") val subtype: TagSubType?
)

fun TagDao.toTagDto(): TagDto {
    return TagDto(
        id = this.id,
        title = this.title,
        type = this.type,
        subtype = this.subtype
    )
}