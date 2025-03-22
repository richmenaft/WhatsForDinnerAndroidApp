package com.example.whatsfordinner.data.source.local

import com.example.whatsfordinner.data.source.local.dish.SelectedTag
import com.example.whatsfordinner.data.source.local.dish.TagStatus
import kotlinx.coroutines.flow.Flow

interface SelectedTagsRepository {

    suspend fun upsertSelectedTags(selectedTags: List<SelectedTag>)
    suspend fun deleteInactiveTags(inactiveTags: List<SelectedTag>)
    suspend fun deleteAllSelectedTags()

    fun getAllSelectedTags(): Flow<List<SelectedTag>>
    fun getSelectedTagsByStatus(status: TagStatus): Flow<List<SelectedTag>>

}