package com.example.whatsfordinner.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsfordinner.data.source.local.OfflineHistoryRepository
import com.example.whatsfordinner.data.source.local.dish.HistoryDish
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(
    val historyRepository: OfflineHistoryRepository
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val historyUiState: StateFlow<HistoryUiState> =
        historyRepository.getHistory().map { HistoryUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HistoryUiState()
            )

    fun clearHistory() {
        viewModelScope.launch {
            historyRepository.deleteDishesHistory()
        }
    }

    fun deleteDishFromHistory(dish: HistoryDish) {
        viewModelScope.launch {
            historyRepository.deleteDishFromHistory(dish)
        }
    }
}


data class HistoryUiState(
    val dishes: List<HistoryDish> = listOf()
)

