package com.example.whatsfordinner.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsfordinner.data.source.local.OfflineSelectedTagsRepository
import com.example.whatsfordinner.data.source.local.dish.SelectedTag
import com.example.whatsfordinner.data.source.local.dish.TagStatus
import com.example.whatsfordinner.data.source.local.dish.toTagDto
import com.example.whatsfordinner.data.source.network.DishCollectionRepository
import com.example.whatsfordinner.data.source.network.dao.TagSubType
import com.example.whatsfordinner.dto.DishDto
import com.example.whatsfordinner.dto.TagDto
import com.example.whatsfordinner.dto.toSelectedTag
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.collections.toSet

private const val TAG = "SharedViewModel"

class SharedViewModel(
    private val dishCollectionRepository: DishCollectionRepository,
    private val offlineSelectedTagsRepository: OfflineSelectedTagsRepository
) : ViewModel() {


    private val _sharedState = MutableStateFlow<SharedState>(SharedState())

    var queryState by mutableStateOf(QueryStatus.Loading)
    val sharedState: StateFlow<SharedState> = _sharedState.asStateFlow()

    init {
        getStandardDishCollection()
    }

    fun getStandardDishCollection() {
        viewModelScope.launch {
            try {
                val dishes = dishCollectionRepository.getStandardDishCollection()
                val appliedTags =
                    offlineSelectedTagsRepository.getSelectedTagsByStatus(TagStatus.APPLY).first()
                val rejectedTags =
                    offlineSelectedTagsRepository.getSelectedTagsByStatus(TagStatus.REJECT).first()
                _sharedState.update {
                    SharedState(
                        dishes = dishes,
                        validDishes = validateDishes(
                            dishes,
                            appliedTags,
                            rejectedTags
                        ),
                        appliedTags = appliedTags,
                        rejectedTags = rejectedTags

                    )
                }
                queryState = QueryStatus.Success
            } catch (e: IOException) {
                queryState = QueryStatus.Error
            }
        }
    }

    suspend fun updateAndValidateDishes(): Boolean {
        val isCompleted = viewModelScope.async {
            try {
                _sharedState.update { sharedUiState ->
                    sharedUiState.copy(
                        validDishes = validateDishes(
                            dishes = sharedUiState.dishes,
                            appliedTags = getAppliedTags(),
                            rejectedTags = getRejectedTags(),
                        )
                    )
                }
                return@async true
            } catch (e: Exception) {
                return@async false
            }
        }
        return isCompleted.await()
    }

    private suspend fun getAppliedTags(): List<SelectedTag> {
        return offlineSelectedTagsRepository.getSelectedTagsByStatus(TagStatus.APPLY).first()
    }

    private suspend fun getRejectedTags(): List<SelectedTag> {
        return offlineSelectedTagsRepository.getSelectedTagsByStatus(TagStatus.REJECT).first()
    }

    private fun validateDishes(
        dishes: List<DishDto>,
        appliedTags: List<SelectedTag>,
        rejectedTags: List<SelectedTag>
    ): List<DishDto> {

        val validDishes = dishes.filter { dish ->
            if (appliedTags.isNotEmpty()) {
                validateDishWithAppliedTag(dish, appliedTags) &&
                        validateDishWithRejectedTag(dish, rejectedTags)
            } else {
                validateDishWithRejectedTag(dish, rejectedTags)
            }
        }
        return validDishes
    }
}

private fun validateDishWithRejectedTag(dish: DishDto, rejectedTags: List<SelectedTag>): Boolean {
    val subtypes: Set<TagSubType> = dish.tags.mapNotNull { tag ->
        tag.subtype
    }.toSet()

    val tagsBySubtype = dish.tags.groupBy {
        it.subtype
    }

    val tagsWithoutSubtypes = dish.tags.filter { tag ->
        tag.subtype == null
    }

    return tagsWithoutSubtypes.all { tag ->
        rejectedTags.none { rejectedTag ->
            rejectedTag.tagId == tag.toSelectedTag().tagId
        }
    }
            &&
            subtypes.all { subtype ->

                val totalTagsForSubtype = tagsBySubtype.getOrDefault(subtype, emptyList()).size
                val rejectedTagsCount = rejectedTags
                    .mapNotNull { rejectedTag ->
                        rejectedTag.toTagDto(dish.tags.find { tag ->
                            tag.id == rejectedTag.tagId
                        })
                    }
                    .count { tag ->
                        tag.subtype == subtype
                    }
                rejectedTagsCount < totalTagsForSubtype
            }
}

private fun validateDishWithAppliedTag(dish: DishDto, appliedTags: List<SelectedTag>): Boolean {
    return appliedTags.all { appliedTag ->
        dish.tags.any() { tag ->
            appliedTag.tagId == tag.toSelectedTag().tagId
        }
    }
}

data class SharedState(
    val dishes: List<DishDto> = listOf(),
    val validDishes: List<DishDto> = listOf(),
    val appliedTags: List<SelectedTag> = listOf(),
    val rejectedTags: List<SelectedTag> = listOf(),
)
