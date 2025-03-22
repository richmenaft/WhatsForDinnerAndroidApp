package com.example.whatsfordinner.data.source.local.dish

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.whatsfordinner.dto.TagDto

@Entity(tableName = "selected_tags")
data class SelectedTag(
    @PrimaryKey
    @ColumnInfo(name = "tag_id")
    val tagId: String,
    @ColumnInfo(name = "tag_status")
    val tagStatus: TagStatus
)

fun SelectedTag.toTagDto(tag: TagDto?): TagDto? {
    return if (tag != null) TagDto(
        id = this.tagId,
        title = tag.title,
        type = tag.type,
        subtype = tag.subtype,
        status = this.tagStatus
    )
    else null
}
