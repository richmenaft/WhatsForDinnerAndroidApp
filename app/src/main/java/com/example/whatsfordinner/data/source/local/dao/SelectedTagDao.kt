package com.example.whatsfordinner.data.source.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.whatsfordinner.data.source.local.dish.SelectedTag
import com.example.whatsfordinner.data.source.local.dish.TagStatus
import com.example.whatsfordinner.data.source.network.dao.toImageDto
import com.example.whatsfordinner.data.source.network.dao.toTagDto
import com.example.whatsfordinner.dto.DishDto
import kotlinx.coroutines.flow.Flow

@Dao
interface SelectedTagDao {
    @Upsert
    suspend fun upsertSelectedTags(selectedTags: List<SelectedTag>)

    @Delete
    suspend fun deleteInactiveTags(inactiveTags: List<SelectedTag>)

    @Query("DELETE FROM selected_tags")
    suspend fun deleteAllSelectedTags()

    @Query("SELECT * FROM selected_tags")
    fun getAllSelectedTags(): Flow<List<SelectedTag>>

    @Query(
        " SELECT * " +
                "FROM selected_tags " +
                "WHERE tag_status == :status"
    )
    fun getSelectedTagsByStatus(status: TagStatus): Flow<List<SelectedTag>>
}

