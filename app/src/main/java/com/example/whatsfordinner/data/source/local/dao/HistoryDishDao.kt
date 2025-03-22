package com.example.whatsfordinner.data.source.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.whatsfordinner.data.source.local.Converters
import com.example.whatsfordinner.data.source.local.dish.HistoryDish
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDishDao {
    @Insert
    suspend fun insertHistoryDish(dish: HistoryDish)

    @Delete
    suspend fun deleteHistoryDish(dish: HistoryDish)

    @Query("DELETE FROM dishes_history")
    suspend fun deleteDishesHistory()

    @Query("SELECT * FROM dishes_history ORDER BY date DESC")
    fun getHistory(): Flow<List<HistoryDish>>
}
