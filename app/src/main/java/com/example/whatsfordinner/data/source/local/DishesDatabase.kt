package com.example.whatsfordinner.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.whatsfordinner.data.source.local.dao.HistoryDishDao
import com.example.whatsfordinner.data.source.local.dao.SelectedTagDao
import com.example.whatsfordinner.data.source.local.dish.HistoryDish
import com.example.whatsfordinner.data.source.local.dish.SelectedTag

@Database(entities = [SelectedTag::class, HistoryDish::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class DishesDatabase : RoomDatabase() {

    abstract fun selectedTagsDao(): SelectedTagDao
    abstract fun history(): HistoryDishDao

    companion object {
        @Volatile
        private var Instance: DishesDatabase? = null

        fun getDatabase(context: Context): DishesDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    DishesDatabase::class.java,
                    "whats_for_dinner_database"
                )
                    .build()
                    .also { Instance = it }
            }
        }
    }
}