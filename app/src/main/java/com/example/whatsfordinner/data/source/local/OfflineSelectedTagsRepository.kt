package com.example.whatsfordinner.data.source.local

import com.example.whatsfordinner.data.source.local.dao.SelectedTagDao
import com.example.whatsfordinner.data.source.local.dish.SelectedTag
import com.example.whatsfordinner.data.source.local.dish.TagStatus
import kotlinx.coroutines.flow.Flow

class OfflineSelectedTagsRepository(
    private val selectedTagsDao: SelectedTagDao
) : SelectedTagsRepository  {
    override suspend fun upsertSelectedTags(selectedTags: List<SelectedTag>) {
        selectedTagsDao.upsertSelectedTags(selectedTags)
    }

    override suspend fun deleteInactiveTags(inactiveTags: List<SelectedTag>) {
        selectedTagsDao.deleteInactiveTags(inactiveTags)
    }

    override suspend fun deleteAllSelectedTags() {
        selectedTagsDao.deleteAllSelectedTags()
    }

    override fun getAllSelectedTags(): Flow<List<SelectedTag>> {
        return selectedTagsDao.getAllSelectedTags()
    }

    override fun getSelectedTagsByStatus(status: TagStatus): Flow<List<SelectedTag>> {
        return selectedTagsDao.getSelectedTagsByStatus(status)
    }

}