package com.example.whatsfordinner.data.source.local

import com.example.whatsfordinner.data.source.local.dish.HistoryDish
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    suspend fun insertHistoryDish(dish: HistoryDish)

    suspend fun deleteDishFromHistory(dish: HistoryDish)

    suspend fun deleteDishesHistory()

    fun getHistory(): Flow<List<HistoryDish>>
}