package com.example.whatsfordinner.ui.tags

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.whatsfordinner.data.source.local.OfflineSelectedTagsRepository
import com.example.whatsfordinner.data.source.local.dish.SelectedTag
import com.example.whatsfordinner.data.source.local.dish.TagStatus
import com.example.whatsfordinner.data.source.local.dish.toTagDto
import com.example.whatsfordinner.data.source.network.dao.TagType
import com.example.whatsfordinner.dto.TagDto
import com.example.whatsfordinner.dto.toSelectedTag
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TagsViewModel(
    private val selectedTagsRepository: OfflineSelectedTagsRepository,
    private val updateAndValidateDishes: suspend () -> Boolean,
    tags: List<TagDto>,
) : ViewModel() {

    // private val _tagsUiState = MutableStateFlow<TagsUiState>(TagsUiState(tags))

    private val _tagsUiState = MutableStateFlow<TagsUiState>(TagsUiState(tags))

    val tagsUiState: StateFlow<TagsUiState> = _tagsUiState.asStateFlow()

    init {
        viewModelScope.launch {
            val selectedTags = selectedTagsRepository.getAllSelectedTags()
                .first()

            _tagsUiState.update { tagsUiState ->
                tagsUiState.copy(
                    tags = tagsUiState.tags.map { tag ->
                        val selectedTag = selectedTags.find {
                            it.tagId == tag.id
                        }

                        if (selectedTag != null)
                            tag.copy(
                                status = selectedTag.tagStatus
                            )
                        else tag
                    }
                )
            }

        }
    }

    suspend fun saveTagsChanges(selectedTags: List<SelectedTag>): Boolean {
        val updateResult: Deferred<Boolean> = viewModelScope.async {
            try {
                selectedTagsRepository.deleteInactiveTags(
                    selectedTags.filter { tag ->
                        tag.tagStatus == TagStatus.INACTIVE
                    })

                selectedTagsRepository.upsertSelectedTags(selectedTags.filter { tag ->
                    tag.tagStatus != TagStatus.INACTIVE
                })
                return@async async{
                    updateAndValidateDishes()
                }.await()
            }
            catch (e: Exception){
                return@async false
        }
    }
        return updateResult.await()
}

fun deleteAllSelectedTags() {
    viewModelScope.launch {
        selectedTagsRepository.deleteAllSelectedTags()
        _tagsUiState.update { tagsUiState ->
            tagsUiState.copy(
                tags = tagsUiState.tags.map {
                    if (it.status != TagStatus.INACTIVE) it.copy(
                        status = TagStatus.INACTIVE
                    )
                    else it
                }
            )
        }
    }
}

fun changeTagType(tagType: TagType) {
    _tagsUiState.update { tagsUiState ->
        tagsUiState.copy(
            currentTagType = tagType
        )
    }
}

fun updateTagStatus(tag: TagDto) {
    val changedTag: TagDto = when (tag.status) {
        TagStatus.INACTIVE -> tag.copy(
            status = TagStatus.APPLY
        )

        TagStatus.APPLY -> tag.copy(
            status = TagStatus.REJECT
        )

        TagStatus.REJECT -> tag.copy(
            status = TagStatus.INACTIVE
        )
    }

    _tagsUiState.update { tagsUiState ->
        val changedTags = tagsUiState.tags
            .filter { it.id != tag.id }
            .plusElement(changedTag)
            .sortedBy {
                it.title
            }

        tagsUiState.copy(
            tags = changedTags
        )
    }
}
}


data class TagsUiState(
    val tags: List<TagDto>,
    val currentTagType: TagType = TagType.INGREDIENT
)