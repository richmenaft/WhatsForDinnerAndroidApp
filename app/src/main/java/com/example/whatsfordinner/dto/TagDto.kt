package com.example.whatsfordinner.dto

import com.example.whatsfordinner.data.source.local.dao.SelectedTagDao
import com.example.whatsfordinner.data.source.local.dish.SelectedTag
import com.example.whatsfordinner.data.source.local.dish.TagStatus
import com.example.whatsfordinner.data.source.network.dao.TagSubType
import com.example.whatsfordinner.data.source.network.dao.TagType

data class TagDto (
    val id: String,
    val title: String,
    val type: TagType,
    val subtype: TagSubType?,
    val status: TagStatus = TagStatus.INACTIVE
)

fun TagDto.toSelectedTag(): SelectedTag {
    return SelectedTag(
        tagId = this.id,
        tagStatus = this.status
    )
}