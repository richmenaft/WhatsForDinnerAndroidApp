package com.example.whatsfordinner.data.source.local.dish

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import java.util.UUID


@Entity(tableName = "dishes_history")
data class HistoryDish(
    @PrimaryKey()
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val imageUrl: String,
    val imageContentDescription: String,
    val date: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
)
