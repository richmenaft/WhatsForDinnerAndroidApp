package com.example.whatsfordinner.dto

import com.example.whatsfordinner.data.source.local.dish.HistoryDish
import com.example.whatsfordinner.data.source.local.dish.SelectedTag

data class DishDto(
    val id: String,
    val title: String,
    val description: String,
    val tags: List<TagDto>,
    val image: ImageDto
)

fun DishDto.toHistoryDish(): HistoryDish = HistoryDish(
    title = title,
    imageUrl = image.url,
    imageContentDescription = image.contentDescription

)