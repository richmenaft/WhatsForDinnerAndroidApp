package com.example.whatsfordinner.data.source.local.dish

import java.util.UUID

data class DishEntity(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val description: String,
    //TODO: изменить тип на String
    val image: ImageEntity,
    val tags: List<TagEntity>,
    val userId: UUID,
)