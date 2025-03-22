package com.example.whatsfordinner.ui.home

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.whatsfordinner.data.source.local.OfflineHistoryRepository
import com.example.whatsfordinner.dto.DishDto
import com.example.whatsfordinner.dto.toHistoryDish
import com.example.whatsfordinner.ui.QueryStatus
import com.example.whatsfordinner.ui.SharedState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update


class HomeViewModel(
    private val validDishes: List<DishDto>,
    private val queryStatus: QueryStatus,
    val sharedState: StateFlow<SharedState>,
    private val historyRepository: OfflineHistoryRepository
) : ViewModel() {

    private val _homeUiState = MutableStateFlow<HomeUiState>(
        HomeUiState(
            queryStatus = queryStatus
        )
    )

    var randomDish = mutableStateOf<DishDto?>(null)

    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    init {
        sharedState.onEach { state ->
            shuffleAndUpdateRandomDishes(
                state.validDishes
            )
        }.launchIn(viewModelScope)
    }

    fun shuffleAndUpdateRandomDishes(validDishes: List<DishDto> = sharedState.value.validDishes) {

        _homeUiState.getAndUpdate { homeUiState ->
            homeUiState.copy(
                dishes = ArrayDeque(validDishes.shuffled())
            )
        }

        popAndUpdateRandomDishes()
    }

    fun popAndUpdateRandomDishes() {

        val dishes = _homeUiState.value.dishes
        if (dishes.isNotEmpty()) {
            val dish = dishes.removeFirst()

            _homeUiState.update { homeUiState ->
                homeUiState.copy(
                    dishes = dishes
                )
            }
            randomDish.value = dish
        } else
            randomDish.value = null
    }

    suspend fun approveDishAndSaveInHistory(dish: DishDto) {
        historyRepository.insertHistoryDish(dish.toHistoryDish())
    }
}

/*

    fun updateRandomDishes(validDishes: List<DishDto> = sharedState.value.validDishes) {
        _homeUiState.getAndUpdate { homeUiState ->
            var nextDish = homeUiState.nextDish
            var currentDish = homeUiState.currentDish
            if (nextDish == null) {
                nextDish = defineRandomDish(
                    currentDish,
                    validDishes
                )
            }
            currentDish = nextDish
            nextDish = defineRandomDish(
                currentDish,
                validDishes
            )
            homeUiState.copy(
                nextDish = nextDish,
                currentDish = currentDish
            )
        }
    }


    private fun defineRandomDish(
        currentDish: DishDto?,
        validDishes: List<DishDto>?
    ): DishDto? {
        if (validDishes.isNullOrEmpty()) return null

        if (validDishes.size == 1) return validDishes.first()

        if (currentDish == null) {
            return validDishes.random()
        }

        var randomDish: DishDto = validDishes.random()
        if (randomDish.id == currentDish.id)
            randomDish = defineRandomDish(currentDish, validDishes)!!

        return randomDish
    }
}

 */

data class HomeUiState(
    /*
    val currentDish: DishDto? = null,
    val nextDish: DishDto? = null,
     */
    val dishes: ArrayDeque<DishDto> = ArrayDeque(),
    val queryStatus: QueryStatus = QueryStatus.Loading
)



