package com.example.whatsfordinner.data.source.network.dao

import com.example.whatsfordinner.dto.DishDto
import com.example.whatsfordinner.dto.ImageDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageDao (
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("content_description") val contentDescription: String,
    @SerialName("url") val url: String,
)

fun ImageDao.toImageDto(): ImageDto {
    return ImageDto(
        id = this.id,
        title = this.title,
        contentDescription = this.contentDescription,
        url = this.url
    )
}