package com.example.whatsfordinner.data.source.network

import com.example.whatsfordinner.data.source.network.dao.DishDao
import com.example.whatsfordinner.data.source.network.dao.toDishDto
import com.example.whatsfordinner.dto.DishDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns

interface DishCollectionRepository {
    suspend fun getStandardDishCollection(): List<DishDto>
}

class StandardDishCollectionRepository(
    private val supabase: SupabaseClient,
) : DishCollectionRepository {
    override suspend fun getStandardDishCollection(): List<DishDto> {
        return supabase.postgrest.from(schema = "public", table = "dish")
            .select(
                Columns.raw("id, title, description, tag(id, title, type, subtype), image(id, title, content_description, url)")
            )
            .decodeList<DishDao>()
            .map { dishDao ->
                dishDao.toDishDto()
            }.toList()
    }
}