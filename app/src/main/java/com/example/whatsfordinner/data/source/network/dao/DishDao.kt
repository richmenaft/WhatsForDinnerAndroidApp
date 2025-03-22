package com.example.whatsfordinner.data.source.network.dao

import com.example.whatsfordinner.dto.DishDto
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DishDao (
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("tag") val tags: List<TagDao>,
    @SerialName("image") val image: ImageDao
)

fun DishDao.toDishDto(): DishDto{
    return DishDto(
        id = this.id,
        title = this.title,
        description = this.description,
        tags = this.tags.map {
            it.toTagDto()
        },
        image = this.image.toImageDto()
    )
}