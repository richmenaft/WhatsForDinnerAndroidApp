package com.example.whatsfordinner.data.source.local

import com.example.whatsfordinner.data.source.local.dao.HistoryDishDao
import com.example.whatsfordinner.data.source.local.dish.HistoryDish
import kotlinx.coroutines.flow.Flow

class OfflineHistoryRepository(
    private val historyDishDao: HistoryDishDao
) : HistoryRepository {
    override suspend fun insertHistoryDish(dish: HistoryDish) {
        historyDishDao.insertHistoryDish(dish)
    }

    override suspend fun deleteDishFromHistory(dish: HistoryDish) {
        historyDishDao.deleteHistoryDish(dish)
    }

    override suspend fun deleteDishesHistory() {
        historyDishDao.deleteDishesHistory()
    }

    override fun getHistory(): Flow<List<HistoryDish>> {
        return historyDishDao.getHistory()
    }

}